use image::{DynamicImage, ImageBuffer, Rgba, GenericImageView};
use std::cmp;

pub struct Decoder;

impl Decoder {
    pub fn decode(input: &DynamicImage) -> DynamicImage {
        // TODO: Analyze the code in this class and reflect the process
        // Note: The variables in this class are deliberately minified

        let (w, h) = input.dimensions();
        let mut output = ImageBuffer::new(w, h);

        for y in (0..h).step_by(4) {
            for x in (0..w).step_by(4) {
                let mut block = [0; 16];

                for by in 0..4 {
                    for bx in 0..4 {
                        let px = x + bx;
                        let py = y + by;

                        if px < w && py < h {
                            let rgb = input.get_pixel(px, py).0;
                            let color = [rgb[0] as i32, rgb[1] as i32, rgb[2] as i32];
                            block[by as usize * 4 + bx as usize] = Self::h_get(&color);
                        }
                    }
                }

                for by in 0..4 {
                    for bx in 0..4 {
                        let px = x + bx;
                        let py = y + by;

                        if px < w && py < h {
                            let out = Self::m_read(&block, bx as i32, by as i32);
                            let out_rgb = ((out[3] as u32) << 24) | ((out[0] as u32) << 16) | ((out[1] as u32) << 8) | (out[2] as u32);
                            output.put_pixel(px, py, Rgba([
                                ((out_rgb >> 16) & 0xFF) as u8,
                                ((out_rgb >> 8) & 0xFF) as u8,
                                (out_rgb & 0xFF) as u8,
                                ((out_rgb >> 24) & 0xFF) as u8,
                            ]));
                        }
                    }
                }
            }
        }

        DynamicImage::ImageRgba8(output)
    }

    const H_A: [u32; 80] = [
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
	0x2a1dec10, 0xb8a50ace, 0xd6f2012d, 0x1f002a30,
    ];

    fn h_h(x: i32) -> u32 {
        let x = cmp::max(0, x - 1);
        let y = Self::H_A[x as usize >> 2];
		
        let z = match x >> 5 {
            0 => 0x43280110,
            1 => 0x30000060,
            2 => 0x80041000,
            3 => 0x08008000,
            4 => 0x20040320,
            5 => 0x00210090,
            6 => 0x000e0000,
            7 => 0x10008000,
            8 => 0x01030000,
            9 => 0xa2010090,
            _ => 0,
        };

        (((z >> (x & 31)) & 1) << 8) | ((y >> ((x & 3) << 3)) & 0xff)
    }

    fn h_get(c: &[i32; 3]) -> u32 {
        let h_1 = [103, 49, 313];
        let h_2 = [103, 112, 119];
		
        let mut s = [0; 3];
        let mut s1 = [0; 3];
        let mut s2 = [0; 3];
		
        for i in 0..3 {
            s[i] = c[i];
            s1[i] = s[i] * h_1[i] + h_1[i];
            s2[i] = s[i] * h_2[i] + h_2[i];
        }
		
        let f1 = (s1[0] + s1[1] + s1[2]) % 321;
        let f2 = (s2[0] + s2[1] + s2[2]) % 321;
		
        (Self::h_h(f1) + Self::h_h(f2)) % 321
    }

    fn m_b(g: &[u32; 16]) -> [u32; 15] {
        let mut r = [0u32; 15];

        for i in 0..16 {
            let mut a = g[i];

            for j in 0..i {
                let t = r[14 - j] * 240 + a;
                r[14 - j] = t & 0xFF;
                a = t >> 8;
            }

            if i < 15 {
                r[14 - i] = a & 0xFF;
            }
        }

        r
    }

    fn m_q(mut v: u32, p: u32, b: u32) -> u32 {
        v = (v << 1) | p;
        v <<= 8 - (b + 1);
        v |= v >> (b + 1);
		
        v
    }

    pub fn m_read(g: &[u32; 16], x: i32, y: i32) -> [u32; 4] {
        let b = Self::m_b(&g);

        let h = ((b[1] & 0x3F) << 2) | ((b[0] & 0x80) >> 6) | 1;
        let k = ((b[2] & 0x1F) << 3) | ((b[1] & 0xE0) >> 5) | 1;
        let l = ((b[3] & 0x0F) << 4) | ((b[2] & 0xF0) >> 4) | 1;
        let m = ((b[4] & 0x07) << 5) | ((b[3] & 0xF8) >> 3) | 1;
        let o = ((b[5] & 0x03) << 6) | ((b[4] & 0xFC) >> 2) | 1;
        let p = ((b[6] & 0x01) << 7) | ((b[5] & 0xFE) >> 1) | 1;
        let mut q = (b[6] & 0x1E) >> 1;
        let mut r = ((b[7] & 0x01) << 3) | ((b[6] & 0xE0) >> 5);

        q = Self::m_q(q, 1, 4);
        r = Self::m_q(r, 1, 4);

        let x1 =                 (b[14] << 23) | (b[13] << 15) | (b[12] << 7) | ((b[11] & 0xFE) >> 1);
        let y1 = (b[11] << 31) | (b[10] << 23) | (b[ 9] << 15) | (b[ 8] << 7) | ((b[ 7] & 0xFE) >> 1);
        let z  = (y & 3) * 4 + (x & 3);

        let d = if z == 0 { 0 } else { (z - 1) * 4 + 3 };
        let e = 32 - d;

        let w = ((((if d < 32 { y1 >> d } else { 0 }) | if e == 32 { 0 } else if e < 0 { x1 >> (-e) } else { x1 << e }) & if z == 0 { 7 } else { 15 }) * 64 + 7) / 15;
        let f = 64 - w;

        [
            ((h * f + k * w + 32) >> 6),
            ((l * f + m * w + 32) >> 6),
            ((o * f + p * w + 32) >> 6),
            ((q * f + r * w + 32) >> 6),
        ]
    }
}
