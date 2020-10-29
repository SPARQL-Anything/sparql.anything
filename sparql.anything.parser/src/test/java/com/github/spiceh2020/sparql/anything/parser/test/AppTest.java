package com.github.spiceh2020.sparql.anything.parser.test;

import com.github.spiceh2020.sparql.anything.tupleurl.TupleURLParser;
import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

public class AppTest {

	@Test
	public void test1() {
		String uri = "tuple:mimeType=application/json,location=file://myfile.json";
		Properties p = new TupleURLParser(uri).getProperties();
		Assert.assertTrue(((Properties) p).containsKey("mimeType"));
		Assert.assertTrue(((Properties) p).containsKey("location"));
	}

	@Test
	public void testArgsInLocation1() {
		String uri = "tuple:mimeType=application/json,location=http://myfile.json?foo\\=bar";
		Properties p = new TupleURLParser(uri).getProperties();
		Assert.assertTrue(((Properties) p).containsKey("location"));
		Assert.assertFalse(((Properties) p).containsKey("foo"));
		Assert.assertEquals(p.get("location"), "http://myfile.json?foo=bar");
	}

	@Test
	public void testArgsInLocation2() {
		String uri = "tuple:mimeType=application/json,location=http://myfile.json?foo\\=bar&tab\\=goal";
		Properties p = new TupleURLParser(uri).getProperties();
		Assert.assertFalse(((Properties) p).containsKey("foo"));
		Assert.assertFalse(((Properties) p).containsKey("tab"));
		Assert.assertEquals(p.get("location"), "http://myfile.json?foo=bar&tab=goal");
	}

	@Test
	public void testArgsInLocation3() {
		String uri = "tuple:mimeType=application/json,location=http://myfile.json?foo\\=bar&tab\\=goal,same=other";
		Properties p = new TupleURLParser(uri).getProperties();
		Assert.assertEquals(p.get("same"), "other");
	}

	@Test
	public void testArgsInLocation4() {
		String uri = "tuple:mimeType=application/json,location=https://myfile.json?foo=bar&tab=goal#hack,same=other";
		Properties p = new TupleURLParser(uri).getProperties();
		Assert.assertFalse(((Properties) p).containsKey("foo"));
	}

	@Test
	public void testArgsInLocation5() {
		String uri = "tuple:mimeType=application/json,location=https://myfile.json?foo=bar&tab=goal#hack,same=other";
		Properties p = new TupleURLParser(uri).getProperties();
		Assert.assertFalse(((Properties) p).containsKey("tab"));
	}

	@Test
	public void testArgsInLocation6() {
		String uri = "tuple:mimeType=application/json,location=https://myfile.json?foo\\=bar&tab\\=goal#hack,same=other";
		Properties p = new TupleURLParser(uri).getProperties();
		Assert.assertEquals(p.get("same"), "other");
	}

	@Test
	public void testArgsInLocation7() {
		String uri = "tuple:mimeType=application/json,location=https://myfile.json?foo\\=bar&tab\\=goal#hack,same=other";
		Properties p = new TupleURLParser(uri).getProperties();
		Assert.assertTrue(p.size() == 3);
	}
}
