/**********************************************************************
Copyright (c) 2014 HubSpot Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 **********************************************************************/
package com.hubspot.jinjava.tree.parse;

import static com.hubspot.jinjava.tree.parse.TokenScannerSymbols.TOKEN_EXPR_START;
import static com.hubspot.jinjava.tree.parse.TokenScannerSymbols.TOKEN_FIXED;
import static com.hubspot.jinjava.tree.parse.TokenScannerSymbols.TOKEN_NOTE;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Ignore;
//import org.junit.Ignore;
import org.junit.Test;

import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.interpret.Context;
import com.hubspot.jinjava.interpret.JinjavaInterpreter;

public class ParserTest {

  JinjavaInterpreter interpreter;
  TokenScanner scanner;
  String script;
  
  @Before
  public void setup() {
    Jinjava jinjava = new Jinjava();
    Context context = new Context();
    interpreter = new JinjavaInterpreter(jinjava, context, jinjava.getGlobalConfig());
  }

  @Test @Ignore("most likely unsupported behavior, not needed in jinja")
  public void test1() {
    script = "{{abc.b}}{% if x %}{\\{abc}}{%endif%}";
    scanner = new TokenScanner(script);
    assertEquals("{{abc.b}}", scanner.next().image);
    assertEquals("if x", scanner.next().content.trim());
    assertEquals("{{abc}}", scanner.next().content.trim());
    assertEquals("{%endif%}", scanner.next().image);
  }

  @Test
  public void test2() {
    script = "{{abc.b}}{% if x %}{{abc{%endif";
    scanner = new TokenScanner(script);
    assertEquals("{{abc.b}}", scanner.next().image);
    assertEquals("if x", scanner.next().content.trim());
    assertEquals("{{abc{%endif", scanner.next().content.trim());
  }

  @Test
  public void test3() {
    script = "{{abc.b}}{% if x %}{{{abc}}{%endif%}";
    scanner = new TokenScanner(script);
    assertEquals("{{abc.b}}", scanner.next().image);
    assertEquals("if x", scanner.next().content.trim());
    Token tk = scanner.next();
    assertEquals("{{{abc}}", tk.image);
    assertEquals(TOKEN_EXPR_START, tk.getType());
    assertEquals("{%endif%}", scanner.next().image);
  }

  @Test
  public void test4() {
    script = "{{abc.b}}{% if x %}{{!abc}}{%endif%}";
    scanner = new TokenScanner(script);
    assertEquals("{{abc.b}}", scanner.next().image);
    assertEquals("if x", scanner.next().content.trim());
    Token tk = scanner.next();
    assertEquals("{{!abc}}", tk.image);
    assertEquals(TOKEN_EXPR_START, tk.getType());
    assertEquals("{%endif%}", scanner.next().image);
  }

  @Test
  public void test5() {
    script = "{{abc.b}}{% if x %}a{{abc}\\}{%endif%}";
    scanner = new TokenScanner(script);
    assertEquals("{{abc.b}}", scanner.next().image);
    assertEquals("if x", scanner.next().content.trim());
    assertEquals("a", scanner.next().content.trim());
    assertEquals("{{abc}\\}{%endif%}", scanner.next().content.trim());
  }

  @Test @Ignore("most likely unsupported behavior unnecessary in jinja")
  public void test6() {
    script = "a{{abc.b}}{% if x 	%}a{\\{abc}}{%endif%}";
    scanner = new TokenScanner(script);
    assertEquals("a", scanner.next().image);
    assertEquals("{{abc.b}}", scanner.next().image);
    assertEquals("if x", scanner.next().content.trim());
    assertEquals("a{{abc}}", scanner.next().content.trim());
    assertEquals("{%endif%}", scanner.next().image);
  }

  @Test
  public void test7() {
    script = "a{{abc.b}}{% if x 	%}a{{abc!}#}%}}}{%endif";
    scanner = new TokenScanner(script);
    assertEquals("a", scanner.next().image);
    assertEquals("{{abc.b}}", scanner.next().image);
    assertEquals("if x", scanner.next().content.trim());
    assertEquals("a", scanner.next().content.trim());
    assertEquals("{{abc!}#}%}}", scanner.next().image);
    assertEquals("}", scanner.next().content.trim());
    assertEquals(TOKEN_FIXED, scanner.next().getType());
  }

  @Test
  public void test8() {
    script = "a{{abc.b}}{% if x 	%}a{{abc}}{%endif{{";
    scanner = new TokenScanner(script);
    assertEquals("a", scanner.next().image);
    assertEquals("{{abc.b}}", scanner.next().image);
    assertEquals("if x", scanner.next().content.trim());
    assertEquals("a", scanner.next().content.trim());
    assertEquals(TOKEN_EXPR_START, scanner.next().getType());
    assertEquals("{%endif{{", scanner.next().content.trim());
  }

  @Test
  public void test9() {
    script = "a{{abc.b}}{% if x 	%}a{{abc}\\}{%endif{";
    scanner = new TokenScanner(script);
    assertEquals("a", scanner.next().image);
    assertEquals("{{abc.b}}", scanner.next().image);
    assertEquals("if x", scanner.next().content.trim());
    assertEquals("a", scanner.next().content.trim());
    assertEquals(TOKEN_FIXED, scanner.next().getType());
  }

  @Test
  public void test10() {
    script = "a{{abc.b}}{% if x %}a{{abc}\\}{{#%endif{";
    scanner = new TokenScanner(script);
    assertEquals("a", scanner.next().image);
    assertEquals("{{abc.b}}", scanner.next().image);
    assertEquals("if x", scanner.next().content.trim());
    assertEquals("a", scanner.next().content.trim());
    assertEquals("{{abc}\\}{", scanner.next().image);
    assertEquals(TOKEN_NOTE, scanner.next().getType());
  }

  @Test
  public void test11() {
    script = "a{#abc.b#}{% if x %}a{{abc}\\}{{{{#endif{";
    scanner = new TokenScanner(script);
    assertEquals("a", scanner.next().image);
    assertEquals("{#abc.b#}", scanner.next().image);
    assertEquals("if x", scanner.next().content.trim());
    assertEquals("a", scanner.next().content.trim());
    assertEquals("{{abc}\\}{{{", scanner.next().content.trim());
    assertEquals("{#endif{", scanner.next().image);
  }

  @Test
  public void test12() {
    script = "{#abc.b#}{% if x %}a{{abc}\\}{{{{#endif{";
    scanner = new TokenScanner(script);
    assertEquals("{#abc.b#}", scanner.next().image);
    assertEquals("if x", scanner.next().content.trim());
    assertEquals("a", scanner.next().content.trim());
    assertEquals("{{abc}\\}{{{", scanner.next().content.trim());
    assertEquals(TOKEN_NOTE, scanner.next().getType());
  }

  @Test
  public void test13() {
    script = "{#abc{#.b#}{#xy{!ad!}{%dbc%}{{dff}}d{#bc#}d#}#}{% if x %}a{{abc}\\}{{{{#endif{";
    scanner = new TokenScanner(script);
    assertEquals("{#abc{#.b#}", scanner.next().image);
  }

  @Test
  public void test14() {
    script = "abc{#.b#}{#xy{!ad!}{%dbc%}{{dff}}d{#bc#}d#}#}{% if x %}a{{abc}\\}{{{{#endif{";
    scanner = new TokenScanner(script);
    assertEquals("abc", scanner.next().image);
    assertEquals("{#.b#}", scanner.next().image);
    assertEquals("{#xy{!ad!}{%dbc%}{{dff}}d{#bc#}", scanner.next().image);
  }

  @Test
  public void test15() {
    script = "abc{#.b#}{#xy{!ad!}{#DD#}{%dbc%}{{dff}}d{#bc#}d#}#}{% if x %}a{{abc}\\}{{{{#endif{";
    scanner = new TokenScanner(script);
    assertEquals("abc", scanner.next().image);
    assertEquals("{#.b#}", scanner.next().image);
    assertEquals("{#xy{!ad!}{#DD#}", scanner.next().image);
  }

  @Test
  public void test16() {
    script = "{#{#abc{#.b#}{#xy{!ad!}{%dbc%}{{dff}}d{#bc#}d#}#}{% if x %}a{{abc}\\}{{{{#endif{";
    scanner = new TokenScanner(script);
    assertEquals("{#{#abc{#.b#}", scanner.next().image);
  }

  @Test
  public void test17() {
    script = "{#abc{#.b#}{#xy{!ad!}{%dbc%}{{dff}}d{#bc#}d#}#}{% if x %}#}a#}{{abc}\\}#}{{{{#endif{";
    scanner = new TokenScanner(script);
    assertEquals("{#abc{#.b#}", scanner.next().image);
  }
}