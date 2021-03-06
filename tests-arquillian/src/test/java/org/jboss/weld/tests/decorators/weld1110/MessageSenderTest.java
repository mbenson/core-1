package org.jboss.weld.tests.decorators.weld1110;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.BeansXml;
import org.jboss.weld.tests.category.Integration;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
@Category(Integration.class)
public class MessageSenderTest {

    @Deployment(testable = false)
    public static WebArchive create() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackage(MessageSenderTest.class.getPackage())
                .setWebXML(new StringAsset(
                        "<web-app>" +
                                "<display-name>jax</display-name>" +
                                "<servlet-mapping>" +
                                "<servlet-name>javax.ws.rs.core.Application</servlet-name>" +
                                "<url-pattern>/rest/*</url-pattern>" +
                                "</servlet-mapping>" +
                                "</web-app>"))
                .addAsWebInfResource(
                        new BeansXml().decorators(MessageDecorator.class), "beans.xml");
    }

    @ArquillianResource
    private URL base;

    @Test
    @Ignore("WELD-1110")
    public void testImpl() throws Exception {
        String response = getWebServiceResponse("rest/message/Hello");
        Assert.assertEquals("Decorated Hello", response);
    }

    @Test
    @Ignore
    public void testFacade() throws Exception {
        String response = getWebServiceResponse("rest/facade/Hello");
        Assert.assertEquals("Decorated Hello", response);
    }

    private String getWebServiceResponse(String urlPath) throws IOException {
        URL url = new URL(base, urlPath);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        try {
            return in.readLine();
        } finally {
            in.close();
        }
    }
}
