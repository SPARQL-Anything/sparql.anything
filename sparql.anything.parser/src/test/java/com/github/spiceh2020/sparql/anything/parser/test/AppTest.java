package com.github.spiceh2020.sparql.anything.parser.test;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.github.spiceh2020.sparql.anything.facadeiri.FacadeIRIParser;

public class AppTest {

	@Test
	public void test1() {
		String uri = "facade-x:mimeType=application/json,location=file://myfile.json";
		Properties p = new FacadeIRIParser(uri).getProperties();
		Assert.assertTrue(((Properties) p).containsKey("mimeType"));
		Assert.assertTrue(((Properties) p).containsKey("location"));
	}

	@Test
	public void testArgsInLocation1() {
		String uri = "facade-x:mimeType=application/json,location=http://myfile.json?foo=bar";
		Properties p = new FacadeIRIParser(uri).getProperties();
		Assert.assertTrue(((Properties) p).containsKey("location"));
		Assert.assertFalse(((Properties) p).containsKey("foo"));
		Assert.assertEquals(p.get("location"), "http://myfile.json?foo=bar");
	}

	@Test
	public void testArgsInLocation2() {
		String uri = "facade-x:mimeType=application/json,location=http://myfile.json?foo=bar&tab=goal";
		Properties p = new FacadeIRIParser(uri).getProperties();
		Assert.assertFalse(((Properties) p).containsKey("foo"));
		Assert.assertFalse(((Properties) p).containsKey("tab"));
		Assert.assertEquals(p.get("location"), "http://myfile.json?foo=bar&tab=goal");
	}

	@Test
	public void testArgsInLocation3() {
		String uri = "facade-x:mimeType=application/json,location=http://myfile.json?foo=bar&tab=goal,same=other";
		Properties p = new FacadeIRIParser(uri).getProperties();
		Assert.assertEquals(p.get("same"), "other");
		Assert.assertEquals(p.get("location"), "http://myfile.json?foo=bar&tab=goal");
	}

	@Test
	public void testArgsInLocation4() {
		String uri = "facade-x:mimeType=application/json,location=https://myfile.json?foo=bar&tab=goal#hack,same=other";
		Properties p = new FacadeIRIParser(uri).getProperties();
		Assert.assertFalse(((Properties) p).containsKey("foo"));
	}

	@Test
	public void testArgsInLocation5() {
		String uri = "facade-x:mimeType=application/json,location=https://myfile.json?foo=bar&tab=goal#hack,same=other";
		Properties p = new FacadeIRIParser(uri).getProperties();
		Assert.assertFalse(((Properties) p).containsKey("tab"));
	}

	@Test
	public void testArgsInLocation6() {
		String uri = "facade-x:mimeType=application/json,location=https://myfile.json?foo=bar&tab=goal#hack,same=other";
		Properties p = new FacadeIRIParser(uri).getProperties();
		Assert.assertEquals(p.get("same"), "other");
	}

	@Test
	public void testArgsInLocation7() {
		String uri = "facade-x:mimeType=application/json,location=https://myfile.json?foo=bar&tab=goal#hack,same=other";
		Properties p = new FacadeIRIParser(uri).getProperties();
		Assert.assertTrue(p.size() == 3);
	}
	
	@Test
	public void testArgsInLocation8() {
		String uri = "facade-x:mimeType=application/json,location=https://myfile.json?fo\\,o=bar&tab=goal#hack,same=other";
		Properties p = new FacadeIRIParser(uri).getProperties();
		Assert.assertTrue(p.size() == 3);
		Assert.assertEquals(p.get("location"), "https://myfile.json?fo,o=bar&tab=goal#hack");
	}
	
	@Test
	public void specialCharsArgs() {
		String uri = "facade-x:mimeType=application/json,location=https://myfile.json?fo\\,o=bar&tab=goal#hack,same=汉字";
		Properties p = new FacadeIRIParser(uri).getProperties();
		Assert.assertEquals(p.get("location"), "https://myfile.json?fo,o=bar&tab=goal#hack");
		Assert.assertEquals(p.get("same"), "汉字");
	}
}
