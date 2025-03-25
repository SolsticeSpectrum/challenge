import java.awt.image.BufferedImage;
import java.awt.Desktop;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Base64;
import java.net.URI;

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
 * Hint: Mode 6
 */

public class EncodingChalenge {

    public static class Encoder {
        public static BufferedImage encode(BufferedImage image) {
            // TODO: Implement the encoding logic
            // Note: This is just a mockup implementation

            return image;
        }
    }

    public static class Decoder {

        public static BufferedImage decode(BufferedImage image) throws Exception {
            // TODO: Analyze the code in this class and reflect the process
            // Note: The variables in this class are deliberately minified

            int w = image.getWidth();
            int h = image.getHeight();

            BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

            for (int y = 0; y < h / 4; y++) {
                for (int x = 0; x < w / 4; x++) {
                    int[] block = new int[16];

                    for (int by = 0; by < 4; by++) {
                        for (int bx = 0; bx < 4; bx++) {
                            int px = x * 4 + bx;
                            int py = y * 4 + by;

                            int rgb = image.getRGB(px, py);
                            int[] color = {
                                (rgb >> 16) & 0xff,
                                (rgb >> 8) & 0xff,
                                rgb & 0xff
                            };

                            block[by * 4 + bx] = H_get(color);
                        }
                    }

                    for (int by = 0; by < 4; by++) {
                        for (int bx = 0; bx < 4; bx++) {
                            int[] out = M_read(block, bx, by);

                            int outX = x * 4 + bx;
                            int outY = y * 4 + by;
                            int outRGB = (out[3] << 24) | (out[0] << 16) | (out[1] << 8) | out[2];

                            output.setRGB(outX, outY, outRGB);
                        }
                    }
                }
            }

            return output;
        }

        private static final int[] H_A = {
            0x00c00000, 0x69d97f0a, 0x00da0005, 0x00fe00ca,
            0x3e3a6b00, 0xb6ce2e0e, 0xcade1037, 0x0029bbc1,
            0xbac3f500, 0x4526154e, 0x99f85900, 0x1367004f,
            0x65003000, 0xe0df94bb, 0xef950000, 0x8dfe0723,
            0xd7c90000, 0xa362002b, 0xba000030, 0xa2f7002b,
            0xd71d83fc, 0x00cb0ff0, 0xf61c9200, 0x2b003b37,
            0x4e2a9d00, 0x0025f2c9, 0x00e06095, 0x014fa90b,
            0xe18f0015, 0xb11106d2, 0x240000bf, 0x005e72a4,
            0x0097c421, 0x347d1799, 0xaf9b000b, 0x7700002f,
            0x31051700, 0x003a00da, 0x95030000, 0x00000bd4,
            0x57a10074, 0x26396a0c, 0x9f67f400, 0xb2257fa7,
            0xa3b30012, 0x7b002349, 0x6a3c0090, 0x42285e94,
            0x662695de, 0x003a00f3, 0x5c85f200, 0xc8b20079,
            0x23171600, 0x2cf40773, 0x2600d1f0, 0x0003dc00,
            0x4e00dbd0, 0x000bd200, 0x2200fccf, 0x33b450ba,
            0x5f960c00, 0x00afc200, 0x334d5b85, 0x0d373224,
            0x00c68600, 0x90000000, 0x04a5bdc0, 0x978119e6,
            0x549a0f26, 0x5918e885, 0x00000004, 0xfd005a3f,
            0xa3542960, 0x06006d2e, 0xfb005fc4, 0x00006384,
            0x2a1dec10, 0xb8a50ace, 0xd6f2012d, 0x1f002a30
        };

        private static int H_h(int x) {
            x = Math.max(0, x - 1);
            int y = H_A[x >> 2];
            int z;

            switch (x >> 5) {
                case 0: z = 0x43280110; break;
                case 1: z = 0x30000060; break;
                case 2: z = 0x80041000; break;
                case 3: z = 0x08008000; break;
                case 4: z = 0x20040320; break;
                case 5: z = 0x00210090; break;
                case 6: z = 0x000e0000; break;
                case 7: z = 0x10008000; break;
                case 8: z = 0x01030000; break;
                case 9: z = 0xa2010090; break;
                default: z = 0;
            }

            return (((z >> (x & 31)) & 1) << 8) | ((y >> ((x & 3) << 3)) & 0xff);
        }

        private static int H_get(int[] c) {
            int[] H_1 = { 103, 49, 313 };
            int[] H_2 = { 103, 112, 119 };

            int[] s = new int[3];
            int[] s1 = new int[3];
            int[] s2 = new int[3];

            for (int i = 0; i < 3; i++) {
                s[i] = c[i];
                s1[i] = s[i] * H_1[i] + H_1[i];
                s2[i] = s[i] * H_2[i] + H_2[i];
            }

            int f1 = (s1[0] + s1[1] + s1[2]) % 321;
            int f2 = (s2[0] + s2[1] + s2[2]) % 321;

            return (H_h(f1) + H_h(f2)) % 321;
        }

        private static int[] M_b(int[] g) {
            int[] r = new int[15];

            for (int i = 0; i < 16; i++) {
                int a = g[i];

                for (int j = 0; j < i; j++) {
                    int t = r[14 - j] * 240 + a;
                    r[14 - j] = t & 0xff;
                    a = t >> 8;
                }

                if (i < 15) r[14 - i] = a & 0xff;
            }

            return r;
        }

        private static int M_q(int v, int p, int b) {
            v = (v << 1) | p;
            v <<= (8 - (b + 1));
            v |= (v >> (b + 1));

            return v;
        }

        private static int[] M_read(int[] g, int x, int y) {
            int[] b = M_b(g);

            int h = ((b[1] & 0x3f) << 2) | ((b[0] & 0x80) >> 6) | 1;
            int k = ((b[2] & 0x1f) << 3) | ((b[1] & 0xe0) >> 5) | 1;
            int l = ((b[3] & 0x0f) << 4) | ((b[2] & 0xf0) >> 4) | 1;
            int m = ((b[4] & 0x07) << 5) | ((b[3] & 0xf8) >> 3) | 1;
            int o = ((b[5] & 0x03) << 6) | ((b[4] & 0xfc) >> 2) | 1;
            int p = ((b[6] & 0x01) << 7) | ((b[5] & 0xfe) >> 1) | 1;
            int q = (b[6] & 0x1e) >> 1;
            int r = ((b[7] & 0x01) << 3) | ((b[6] & 0xe0) >> 5);

            q = M_q(q, 1, 4);
            r = M_q(r, 1, 4);

            int x1 = (b[14] << 23) | (b[13] << 15) | (b[12] << 7) | ((b[11] & 0xfe) >> 1);
            int y1 = (b[11] << 31) | (b[10] << 23) | (b[9] << 15) | (b[8] << 7) | ((b[7] & 0xfe) >> 1);
            int z = (y & 3) * 4 + (x & 3);

            int d = z == 0 ? 0 : (z - 1) * 4 + 3;
            int e = 32 - d;

            int w = ((((d < 32 ? (y1 >>> d) : 0) | (e == 32 ? 0 : (e < 0 ? (x1 >>> (-e)) : (x1 << e)))) & (z == 0 ? 7 : 15)) * 64 + 7) / 15;
            int f = 64 - w;

            return new int[] {
                ((h * f + k * w + 32) >> 6),
                ((l * f + m * w + 32) >> 6),
                ((o * f + p * w + 32) >> 6),
                ((q * f + r * w + 32) >> 6)
            };
        }
    }

    public static boolean compareImages(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight())
            return false;

        for (int y = 0; y < img1.getHeight(); y++)
            for (int x = 0; x < img1.getWidth(); x++)
                if (img1.getRGB(x, y) != img2.getRGB(x, y))
                    return false;

        return true;
    }

    public static String imageToBase64(BufferedImage image) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    public static BufferedImage base64ToImage(String base64) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(base64);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        return ImageIO.read(bais);
    }
    
    public static BufferedImage loadImageFromFile(String filename) throws Exception {
        return ImageIO.read(new File(filename));
    }

    public static String imageToUri(BufferedImage image) throws Exception {
        return "data:image/png;base64," + imageToBase64(image);
    }

    public static String resultsPage(ArrayList < TestResult > testResults) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>");
        html.append("body { font-family: Arial, sans-serif; } .test-case { margin-bottom: 20px; border: 1px solid #ddd; padding: 10px; } ");
        html.append(".pass { color: green; } .fail { color: red; } img { max-width: 200px; max-height: 200px; margin: 5px; }");
        html.append("</style></head><body><h1>Encoding Challenge Test Results</h1>");
        
		int i = 0;

        for (TestResult result: testResults) {
            String statusClass = result.passed ? "pass" : "fail";
            html.append("<div class=\"test-case\">")
                .append("<h2>Test Case ").append(i + 1).append(": ")
				.append(result.name).append(" - <span class=\"").append(statusClass).append("\">")
                .append(result.passed ? "PASS" : "FAIL").append("</span></h2>")
                .append("<div><h3>Input Image:</h3><img src=\"").append(result.inputUri).append("\" alt=\"Input Image\"></div>")
                .append("<div><h3>Expected Output:</h3><img src=\"").append(result.expectedUri).append("\" alt=\"Expected Output\"></div>")
                .append("<div><h3>Actual Output:</h3><img src=\"").append(result.actualUri).append("\" alt=\"Actual Output\"></div>")
                .append("</div>");
			
			i++;
        }

        html.append("</body></html>");
        return html.toString();
    }

    public static class TestResult {
        String name;
        boolean passed;
        String inputUri;
        String expectedUri;
        String actualUri;

        public TestResult(String name, boolean passed, String inputUri, String expectedUri, String actualUri) {
            this.name = name;
            this.passed = passed;
            this.inputUri = inputUri;
            this.expectedUri = expectedUri;
            this.actualUri = actualUri;
        }
    }

    public static void runTests() throws Exception {
        ArrayList < TestResult > results = new ArrayList < > ();

        String[] tests = {
            "4x4 blocks",
            "Simple colors",
            "Merging colors",
            "Complex scene (no patterns)"
        };
        String[] inputFiles = {
            "samples/4x4 blocks out.png",
            "samples/simple colors out.png",
            "samples/merging colos out.png",
            "samples/complex scene out.png"
        };
        String[] expectedFiles = {
            "samples/4x4 blocks in.png",
            "samples/simple colors in.png",
            "samples/merging colors in.png",
            "samples/complex scene in.png"
        };

        for (int i = 0; i < tests.length; i++) {
            String testName = tests[i];
            
            BufferedImage inputImage = loadImageFromFile(inputFiles[i]);
            BufferedImage expectedImage = loadImageFromFile(expectedFiles[i]);

            BufferedImage encodedImage = Encoder.encode(inputImage);
            BufferedImage decodedImage = Decoder.decode(encodedImage);

            boolean passed = compareImages(decodedImage, inputImage);

            results.add(new TestResult(testName, passed, imageToUri(inputImage), imageToUri(expectedImage), imageToUri(encodedImage)));

            System.out.println(testName + ": " + (passed ? "PASS" : "FAIL"));
        }

        String html = resultsPage(results);
        File temp = File.createTempFile("test_results", ".html");

        FileWriter writer = new FileWriter(temp);
        writer.write(html);
        writer.close();

        if (!Desktop.isDesktopSupported()) System.err.println("Desktop not supported!");

        Desktop desktop = Desktop.getDesktop();
        desktop.browse(temp.toURI());
    }

    public static void main(String[] args) throws Exception {
        runTests();
    }
}
