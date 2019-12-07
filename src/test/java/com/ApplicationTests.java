package com;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.BitSet;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Test
	public void contextLoads() {
		System.out.println("hello world");
	}

	@Test
	public void byteArray2BitSet() {
		BitSet bitSet = new BitSet();
		int cnt = 0;
		byte[] bytes = new byte[] {-1};
		for (byte aByte : bytes) {
			int mask = 1 << 7;
			for (int j = 0; j < 8; j++) {
				int uByte = aByte & 0xff;
				System.out.println("mask " + mask + ",uByte " + uByte);
				System.out.println((uByte & mask) != 0 ? "1" : "0");
				bitSet.set(cnt, (aByte & mask) == 1);
				cnt++;
				mask >>>= 1;
			}
		}
	}

}
