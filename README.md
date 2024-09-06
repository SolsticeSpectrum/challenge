# Encoding Challenge

## Overview

In this challenge, your goal is to modify the `Encoder` class to correctly transform an image into an encoded format, so that it can later be decoded using the provided `Decoder` class. The key is to make sure that the output of the `Encoder` matches the input of the `Decoder`, ensuring that the original image can be reconstructed.

## Tasks

1. **Analyze the Decoder**: Begin by studying the `Decoder` class to understand how it decodes the encoded image.
2. **Implement the Encoder**: Modify the `Encoder` class to correctly encode images in such a way that they can be decoded back to the original form.
3. **Ensure Accuracy**: After implementing the `Encoder`, verify that passing an image through the `Encoder` followed by the `Decoder` results in the original image.
4. **Replace the Mockup Encoder**: Replace the placeholder Encoder with your actual implementation.
5. **Test Your Solution**: Use the provided tester to check the accuracy and correctness of your solution.
6. **Color Palette**: There is a `colors.txt` file provided. Although its usage is unknown, you may need to implement it in your solution.
7. **Hint**: Mode 6.

## Requirements

The following Python packages are required to run the challenge:

- `numpy`
- `Pillow`

## Instructions

### Step 1: Clone the Repository

To get started, clone the repository using Git:

```bash
git clone <repository-url>
cd <repository-name>
```

### Step 2: Install Dependencies

Make sure you have Python installed on your system. Then, install the necessary packages:

```bash
pip install -r requirements.txt
```

### Step 3: Start Solving the Challenge

Open the `EncodingChallenge.py` file and locate the `Encoder` class. You need to implement the `encode` function inside this class.
Once you've implemented the `encode` function, you can run the provided test cases to verify your solution.
