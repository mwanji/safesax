/**
 * Copyright (C) 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package safesax;

import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class DefaultNamespaceTest extends TestCase {

  protected static final String ATOM_NAMESPACE = "http://www.w3.org/2005/Atom";

  public static void parse(String xml, ContentHandler contentHandler)
      throws SAXException {
    try {
      Parsers.parse(new StringReader(xml), contentHandler);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  DefaultNamespaceTest.ElementCounter rootCounter = new DefaultNamespaceTest.ElementCounter();
  DefaultNamespaceTest.ElementCounter entryCounter = new DefaultNamespaceTest.ElementCounter();
  DefaultNamespaceTest.TextElementCounter idCounter = new DefaultNamespaceTest.TextElementCounter();

  public void testDefaultNamespaceSetForAllOptionalElements() throws SAXException {
    String xml = "<feed xmlns='http://www.w3.org/2005/Atom'>\n"
        + "<entry>\n"
        + "<id>a</id>\n"
        + "</entry>\n"
        + "<entry>\n"
        + "<id>b</id>\n"
        + "</entry>\n"
        + "</feed>\n";

    RootElement root = new RootElement(ATOM_NAMESPACE, "feed");
    Element entry = root.getChild("entry");
    Element id = entry.getChild("id");

    root.setElementListener(rootCounter);
    entry.setElementListener(entryCounter);
    id.setTextElementListener(idCounter);

    parse(xml, root.getContentHandler());

    assertEquals(1, rootCounter.starts);
    assertEquals(1, rootCounter.ends);
    assertEquals(2, entryCounter.starts);
    assertEquals(2, entryCounter.ends);
    assertEquals(2, idCounter.starts);
    assertEquals("ab", idCounter.bodies);
  }
  
  public void testDefaultNamespaceOverriddenInOptionalElement() throws Exception {
    String xml = "<feed xmlns='http://www.w3.org/2005/Atom'>\n"
      + "<entry xmlns='http://www.other.com'>\n"
      + "<id>a</id>\n"
      + "</entry>\n"
      + "<entry>\n"
      + "<id>b</id>\n"
      + "</entry>\n"
      + "</feed>\n";

    RootElement root = new RootElement(ATOM_NAMESPACE, "feed");
    Element entry = root.getChild("entry");
    Element entryInOtherNamespace = root.getChild("http://www.other.com", "entry");
    Element id = entry.getChild("id");
    Element idInOtherNamespace = entryInOtherNamespace.getChild("id");

    root.setElementListener(rootCounter);
    entry.setElementListener(entryCounter);
    DefaultNamespaceTest.ElementCounter entryInOtherNamespaceCounter = new DefaultNamespaceTest.ElementCounter();
		entryInOtherNamespace.setElementListener(entryInOtherNamespaceCounter);
    id.setTextElementListener(idCounter);
    DefaultNamespaceTest.TextElementCounter idInOtherNamespaceCounter = new DefaultNamespaceTest.TextElementCounter();
    idInOtherNamespace.setTextElementListener(idInOtherNamespaceCounter);
    

    parse(xml, root.getContentHandler());

    assertEquals(1, rootCounter.starts);
    assertEquals(1, rootCounter.ends);
    assertEquals(1, entryCounter.starts);
    assertEquals(1, entryCounter.ends);
    assertEquals(1, entryInOtherNamespaceCounter.starts);
    assertEquals(1, entryInOtherNamespaceCounter.ends);
    assertEquals(1, idCounter.starts);
    assertEquals("b", idCounter.bodies);
    assertEquals(1, idInOtherNamespaceCounter.starts);
    assertEquals("a", idInOtherNamespaceCounter.bodies);
  }

  public void testDefaultNamespaceSetForAllRequiredElements() throws SAXException {
    String xml = "<feed xmlns='http://www.w3.org/2005/Atom'>\n"
        + "<entry>\n"
        + "<id>a</id>\n"
        + "</entry>\n"
        + "<entry>\n"
        + "<id>b</id>\n"
        + "</entry>\n"
        + "</feed>\n";

    RootElement root = new RootElement(ATOM_NAMESPACE, "feed");
    Element entry = root.requireChild("entry");
    Element id = entry.requireChild("id");

    root.setElementListener(rootCounter);
    entry.setElementListener(entryCounter);
    id.setTextElementListener(idCounter);

    parse(xml, root.getContentHandler());

    assertEquals(1, rootCounter.starts);
    assertEquals(1, rootCounter.ends);
    assertEquals(2, entryCounter.starts);
    assertEquals(2, entryCounter.ends);
    assertEquals(2, idCounter.starts);
    assertEquals("ab", idCounter.bodies);
  }
  
  public void testDefaultNamespaceOverriddenInRequiredElement() throws Exception {
    String xml = "<feed xmlns='http://www.w3.org/2005/Atom'>\n"
      + "<entry xmlns='http://www.other.com'>\n"
      + "<id>a</id>\n"
      + "</entry>\n"
      + "<entry>\n"
      + "<id>b</id>\n"
      + "</entry>\n"
      + "</feed>\n";

    RootElement root = new RootElement(ATOM_NAMESPACE, "feed");
    Element entry = root.requireChild("entry");
    Element entryInOtherNamespace = root.requireChild("http://www.other.com", "entry");
    Element id = entry.requireChild("id");
    Element idInOtherNamespace = entryInOtherNamespace.requireChild("id");

    root.setElementListener(rootCounter);
    entry.setElementListener(entryCounter);
    DefaultNamespaceTest.ElementCounter entryInOtherNamespaceCounter = new DefaultNamespaceTest.ElementCounter();
		entryInOtherNamespace.setElementListener(entryInOtherNamespaceCounter);
    id.setTextElementListener(idCounter);
    DefaultNamespaceTest.TextElementCounter idInOtherNamespaceCounter = new DefaultNamespaceTest.TextElementCounter();
    idInOtherNamespace.setTextElementListener(idInOtherNamespaceCounter);
    

    parse(xml, root.getContentHandler());

    assertEquals(1, rootCounter.starts);
    assertEquals(1, rootCounter.ends);
    assertEquals(1, entryCounter.starts);
    assertEquals(1, entryCounter.ends);
    assertEquals(1, entryInOtherNamespaceCounter.starts);
    assertEquals(1, entryInOtherNamespaceCounter.ends);
    assertEquals(1, idCounter.starts);
    assertEquals("b", idCounter.bodies);
    assertEquals(1, idInOtherNamespaceCounter.starts);
    assertEquals("a", idInOtherNamespaceCounter.bodies);
  }

  static class ElementCounter implements ElementListener {

    int starts = 0;
    int ends = 0;

    public void start(Attributes attributes) {
      starts++;
    }

    public void end() {
      ends++;
    }
  }

  static class TextElementCounter implements TextElementListener {

    int starts = 0;
    String bodies = "";

    public void start(Attributes attributes) {
      starts++;
    }

    public void end(String body) {
      this.bodies += body;
    }
  }
}
