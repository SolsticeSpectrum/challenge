use base64::prelude::*;
use image::{DynamicImage, ImageFormat, GenericImageView};
use std::io::Cursor;
use std::fs;
use std::env;
use std::path::PathBuf;
use anyhow::{Result, Context};

mod encoder;
mod decoder;

use encoder::Encoder;
use decoder::Decoder;

/* Encoding Challenge
 * 
 * Goal: Adjust the Encoder class to reflect the process of the Decoder.
 * 
 * Tasks:
 * 1. Analyze the Decoder class and understand how it works.
 * 2. Implement the Encoder so that it can convert the decoded image back into its encoded form.
 * 3. Ensure that when you pass an image through the Encoder and then through the Decoder, you obtain the original image.
 * 4. Replace the mockup Encoder with your implementation.
 * 5. Use the provided tester to verify the correctness of the solution.
 * 6. You also have access to the colors.txt file, which's usage is unknown.
 *
 * Note: The important code is in decoder.rs and encoder.rs
 * Hint: Mode 6
 */

fn compare_images(img1: &DynamicImage, img2: &DynamicImage) -> bool {
    img1.as_bytes() == img2.as_bytes()
}

fn image_to_base64(img: &DynamicImage) -> Result<String> {
    let mut image_data: Vec<u8> = Vec::new();
    img.write_to(&mut Cursor::new(&mut image_data), ImageFormat::Png)
        .context("Failed to write image to buffer")?;
    Ok(BASE64_STANDARD.encode(image_data))
}

fn base64_to_image(string: &str) -> Result<DynamicImage> {
    let decoded = BASE64_STANDARD.decode(string).context("Failed to decode base64")?;
    image::load_from_memory(&decoded).context("Failed to load image from memory")
}

fn load_image_from_file(filename: &str) -> Result<DynamicImage> {
    image::open(filename).context("Failed to load image from file")
}

fn results_page(test_results: &[TestResult]) -> String {
    let mut html = String::from(
    r#"<html>
    <head>
        <style>
            body { font-family: Arial, sans-serif; }
            .test-case { margin-bottom: 20px; border: 1px solid #ddd; padding: 10px; }
            .pass { color: green; }
            .fail { color: red; }
            img { max-width: 200px; max-height: 200px; margin: 5px; }
        </style>
    </head>
    <body>
        <h1>Encoding Challenge Test Results</h1>"#
    );

    for (i, result) in test_results.iter().enumerate() {
        let status_class = if result.passed { "pass" } else { "fail" };
        html.push_str(&format!(
            r#"<div class="test-case">
                <h2>Test Case {}: {} - <span class="{}">{}</span></h2>
                <div><h3>Input Image:</h3><img src="data:image/png;base64,{}" alt="Input Image"></div>
                <div><h3>Expected Output:</h3><img src="data:image/png;base64,{}" alt="Expected Output"></div>
                <div><h3>Actual Output:</h3><img src="data:image/png;base64,{}" alt="Actual Output"></div>
            </div>"#,
            i + 1,
            result.name,
            status_class,
            if result.passed { "PASS" } else { "FAIL" },
            result.input_uri,
            result.expected_uri,
            result.actual_uri
        ));
    }

    html.push_str("</body></html>");
    html
}

struct TestResult {
    name: String,
    passed: bool,
    input_uri: String,
    expected_uri: String,
    actual_uri: String,
}

fn run_tests() -> Result<()> {
    let tests = vec![
        TestCase {
            name: String::from("4x4 blocks"),
            expected_file: String::from("samples/4x4 blocks in.png"),
            input_file: String::from("samples/4x4 blocks out.png"),
        },
        TestCase {
            name: String::from("Simple colors"),
            expected_file: String::from("samples/simple colors in.png"),
            input_file: String::from("samples/simple colors out.png"),
        },
        TestCase {
            name: String::from("Merging colors"),
            expected_file: String::from("samples/merging colors in.png"),
            input_file: String::from("samples/merging colos out.png"),
        },
        TestCase {
            name: String::from("Complex scene (no patterns)"),
            expected_file: String::from("samples/complex scene in.png"),
            input_file: String::from("samples/complex scene out.png"),
        },
    ];

    let mut results = Vec::new();
    for case in &tests {
        println!("Running test case: {}", case.name);

        let input_image = load_image_from_file(&case.input_file)?;
        let expected_image = load_image_from_file(&case.expected_file)?;

        let encoded_image = Encoder::encode(&input_image);
        let decoded_image = Decoder::decode(&encoded_image);

        let passed = compare_images(&decoded_image, &input_image);

        let result = TestResult {
            name: case.name.clone(),
            passed,
            input_uri: image_to_base64(&input_image)?,
            expected_uri: image_to_base64(&expected_image)?,
            actual_uri: image_to_base64(&encoded_image)?,
        };

        results.push(result);
        println!("{}", if passed { "PASS" } else { "FAIL" });
    }

    let html = results_page(&results);

    let temp = temp_file("test_results.html")?;
    fs::write(&temp, html)?;

    if let Err(err) = open::that(temp) {
        eprintln!("Failed to open browser: {}", err);
    }

    Ok(())
}

fn temp_file(filename: &str) -> Result<PathBuf> {
    let mut temp = env::temp_dir();
    temp.push(filename);
    Ok(temp)
}

struct TestCase {
    name: String,
    input_file: String,
    expected_file: String,
}

fn main() -> Result<()> {
    run_tests()?;
    Ok(())
}
