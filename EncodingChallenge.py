import numpy as np
from PIL import Image
import io
import base64
import webbrowser
import tempfile
import os


# Encoding Challenge
# 
# Goal: Adjust the Encoder class to reflect the process of the Decoder.
# 
# Tasks:
# 1. Analyze the Decoder class and understand how it works.
# 2. Implement the Encoder so that it can convert the decoded image back into its encoded form.
# 3. Ensure that when you pass an image through the Encoder and then through the Decoder, you obtain the original image.
# 4. Replace the mockup Encoder with your implementation.
# 5. Use the provided tester to verify the correctness of the solution.
# 6. You also have access to the colors.txt file, which's usage is unknown.
#
# Hint: Mode 6


class Encoder:
    @staticmethod
    def encode(image):
        # TODO: Implement the encoding logic
        # Note: This is just a mockup implementation
        
        return image


class Decoder:
    @staticmethod
    def decode(encoded_image):
        # TODO: Analyze the code in this class and reflect the process
        # Note: The variables in this class are deliberately minified
        
        w, h = encoded_image.size
        output = Image.new('RGBA', (w, h))

        for y in range(0, h, 4):
            for x in range(0, w, 4):
                block = []
                for by in range(4):
                    for bx in range(4):
                        px, py = x + bx, y + by
                        rgb = encoded_image.getpixel((px, py))[:3]
                        block.append(Decoder.H_get(rgb))

                for by in range(4):
                    for bx in range(4):
                        out = Decoder.M_read(block, bx, by)
                        out_x, out_y = x + bx, y + by
                        output.putpixel((out_x, out_y), tuple(out))

        return output


    H_A = [
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
    ]

    def H_h(x):
        x = max(0, x - 1)
        y = Decoder.H_A[x >> 2]
        
        z1 = [
            0x43280110, 0x30000060, 0x80041000, 0x08008000, 0x20040320,
            0x00210090, 0x000e0000, 0x10008000, 0x01030000, 0xa2010090
        ]
        
        z = z1[x >> 5] if x >> 5 < len(z1) else 0
        
        return (((z >> (x & 31)) & 1) << 8) | ((y >> ((x & 3) << 3)) & 0xff)

    def H_get(c):
        H_1 = [103, 49, 313]
        H_2 = [103, 112, 119]

        s = c
        s1 = [s[i] * H_1[i] + H_1[i] for i in range(3)]
        s2 = [s[i] * H_2[i] + H_2[i] for i in range(3)]

        f1 = sum(s1) % 321
        f2 = sum(s2) % 321

        return (Decoder.H_h(f1) + Decoder.H_h(f2)) % 321

    def M_b(g):
        r = [0] * 15

        for i in range(16):
            a = g[i]

            for j in range(i):
                t = r[14 - j] * 240 + a
                r[14 - j] = t & 0xff
                a = t >> 8

            if i < 15:
                r[14 - i] = a & 0xff

        return r

    def M_q(v, p, b):
        v = (v << 1) | p
        v <<= (8 - (b + 1))
        v |= (v >> (b + 1))
        return v

    def M_read(g, x, y):
        b = Decoder.M_b(g)

        h = ((b[1] & 0x3f) << 2) | ((b[0] & 0x80) >> 6) | 1
        k = ((b[2] & 0x1f) << 3) | ((b[1] & 0xe0) >> 5) | 1
        l = ((b[3] & 0x0f) << 4) | ((b[2] & 0xf0) >> 4) | 1
        m = ((b[4] & 0x07) << 5) | ((b[3] & 0xf8) >> 3) | 1
        o = ((b[5] & 0x03) << 6) | ((b[4] & 0xfc) >> 2) | 1
        p = ((b[6] & 0x01) << 7) | ((b[5] & 0xfe) >> 1) | 1
        q = (b[6] & 0x1e) >> 1
        r = ((b[7] & 0x01) << 3) | ((b[6] & 0xe0) >> 5)

        q = Decoder.M_q(q, 1, 4)
        r = Decoder.M_q(r, 1, 4)

        x1 = (b[14] << 23) | (b[13] << 15) | (b[12] << 7) | ((b[11] & 0xfe) >> 1)
        y1 = (b[11] << 31) | (b[10] << 23) | (b[9] << 15) | (b[8] << 7) | ((b[7] & 0xfe) >> 1)
        z  = (y & 3) * 4 + (x & 3)

        d = 0 if z == 0 else (z - 1) * 4 + 3
        e = 32 - d

        w = ((((y1 >> d if d < 32 else 0) | (0 if e == 32 else (x1 >> (-e) if e < 0 else x1 << e))) & (7 if z == 0 else 15)) * 64 + 7) // 15
        f = 64 - w

        return [
            ((h * f + k * w + 32) >> 6),
            ((l * f + m * w + 32) >> 6),
            ((o * f + p * w + 32) >> 6),
            ((q * f + r * w + 32) >> 6)
        ]


def compare_images(img1, img2):
    return np.array_equal(np.array(img1), np.array(img2))


def image_to_base64(image):
    buffered = io.BytesIO()
    image.save(buffered, format="PNG")
    return base64.b64encode(buffered.getvalue()).decode()


def base64_to_image(string):
    bytes = base64.b64decode(string)
    return Image.open(io.BytesIO(bytes))

def load_image_from_file(filename):
    return Image.open(filename)


def image_to_uri(image):
    return f"data:image/png;base64,{image_to_base64(image)}"


def results_page(test_results):
    html = """
    <html>
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
        <h1>Encoding Challenge Test Results</h1>
    """
    
    for i, result in enumerate(test_results):
        status_class = "pass" if result['passed'] else "fail"
        html += f"""
        <div class="test-case">
            <h2>Test Case {i + 1}: {result['name']} - <span class="{status_class}">{'PASS' if result['passed'] else 'FAIL'}</span></h2>
            <div>
                <h3>Input Image:</h3>
                <img src="{result['input_uri']}" alt="Input Image">
            </div>
            <div>
                <h3>Expected Output:</h3>
                <img src="{result['expected_uri']}" alt="Expected Output">
            </div>
            <div>
                <h3>Actual Output:</h3>
                <img src="{result['actual_uri']}" alt="Actual Output">
            </div>
        </div>
        """

    html += """
    </body>
    </html>
    """
    
    return html


tests = [
    {
        'name': '4x4 blocks',
        'expected_file': 'samples/4x4 blocks in.png',
        'input_file': 'samples/4x4 blocks out.png'
    },
    {
        'name': 'Simple colors',
        'expected_file': 'samples/simple colors in.png',
        'input_file': 'samples/simple colors out.png'
    },
    {
        'name': 'Merging colors',
        'expected_file': 'samples/merging colors in.png',
        'input_file': 'samples/merging colos out.png'
    },
    {
        'name': 'Complex scene (no patterns)',
        'expected_file': 'samples/complex scene in.png',
        'input_file': 'samples/complex scene out.png'
    }
]


def run_tests():
    results = []
    
    for i, case in enumerate(tests):
        print(f"Running test case {i + 1}: {case['name']}")
        
        input_image = load_image_from_file(case['input_file'])
        expected_image = load_image_from_file(case['expected_file'])
        
        encoded_image = Encoder.encode(input_image)
        decoded_image = Decoder.decode(encoded_image)
        
        passed = compare_images(decoded_image, input_image)
        
        result = {
            'name': case['name'],
            'passed': passed,
            'input_uri': image_to_uri(input_image),
            'expected_uri': image_to_uri(expected_image),
            'actual_uri': image_to_uri(encoded_image)
        }
        
        results.append(result)
        
        print("PASS" if passed else "FAIL")
        print()

    html = results_page(results)
    with tempfile.NamedTemporaryFile('w', delete=False, suffix='.html') as f:
        f.write(html)
    
    results_url = 'file://' + os.path.realpath(f.name)
    print(f"Test results page: {results_url}")
    webbrowser.open(results_url)


if __name__ == "__main__":
    run_tests()
