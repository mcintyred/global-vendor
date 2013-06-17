package gv.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.junit.Test;
import org.springframework.ws.context.DefaultMessageContext;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.pox.dom.DomPoxMessage;
import org.springframework.ws.pox.dom.DomPoxMessageFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class XPathTest {
	
	@Test
	public void shouldWork() throws Exception {
		String xml = "<gv:line productId=\"arse\"   xmlns:gv=\"http://www.gv.demo/api\" />";
//				"<gv:order xmlns:gv=\"http://www.gv.demo/api\">" +
//					"<gv:lines>" +
//						"<gv:line productId=\"22\" qty=\"44\"/>" +
//					"</gv:lines>" +
//				"</gv:order>";
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

		Document document = documentBuilder.parse(new InputSource(new StringReader(xml)));
		
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		xpath.setNamespaceContext(new NS());
		
		
		String all = xpath.evaluate("//gv:line/@productId", document);
		

	}
	
	public class NS implements NamespaceContext {

		@Override
		public String getNamespaceURI(String prefix) {
			return "http://www.gv.demo/api";
		}

		@Override
		public String getPrefix(String namespaceURI) {
			return "gv";
		}

		@Override
		public Iterator getPrefixes(String namespaceURI) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

}
