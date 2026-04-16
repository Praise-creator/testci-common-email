package praise_olatide;

import static org.junit.Assert.*;

import java.util.Date;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.EmailConstants;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

//Unit tests for selected methods in org.apache.commons.mail.Email.
 
public class EmailTest {

    private SimpleEmail email;

    @Before
    public void setUp() {
        email = new SimpleEmail();
    }

    @After
    public void tearDown() {
        email = null;
    }

    // addBcc(String... emails) 

    @Test
    public void testAddBccVarargsValid() throws Exception {
        email.addBcc(new String[] { "bcc1@example.com", "bcc2@example.com" });
        assertEquals(2, email.getBccAddresses().size());
        assertEquals("bcc1@example.com", email.getBccAddresses().get(0).getAddress());
        assertEquals("bcc2@example.com", email.getBccAddresses().get(1).getAddress());
    }
    
    @Test(expected = EmailException.class)
    public void testAddBccVarargsNullThrows() throws Exception {
        email.addBcc((String[]) null);
    }

    @Test(expected = EmailException.class)
    public void testAddBccVarargsEmptyThrows() throws Exception {
        email.addBcc(new String[] {});
    }

    // addCc(String email) 

    @Test
    public void testAddCcSingleValid() throws Exception {
        email.addCc("cc@example.com");
        assertEquals(1, email.getCcAddresses().size());
        assertEquals("cc@example.com", email.getCcAddresses().get(0).getAddress());
    }

    @Test(expected = EmailException.class)
    public void testAddCcSingleInvalidEmailThrows() throws Exception {
        email.addCc("not-an-email");
    }

    // addHeader(String name, String value) 

    @Test
    public void testAddHeaderValid() {
        email.addHeader("X-Tracking", "abc123");
        
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddHeaderEmptyNameThrows() {
        email.addHeader("", "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddHeaderNullValueThrows() {
        email.addHeader("X-Test", null);
    }

    // addReplyTo(String email, String name) 

    @Test
    public void testAddReplyToValid() throws Exception {
        email.addReplyTo("reply@example.com", "Reply Person");
        assertEquals(1, email.getReplyToAddresses().size());
        InternetAddress addr = email.getReplyToAddresses().get(0);
        assertEquals("reply@example.com", addr.getAddress());
        assertEquals("Reply Person", addr.getPersonal());
    }

    @Test(expected = EmailException.class)
    public void testAddReplyToInvalidEmailThrows() throws Exception {
        email.addReplyTo("bad-email", "Name");
    }

    // setFrom(String email) 

    @Test
    public void testSetFromValid() throws Exception {
        email.setFrom("from@example.com");
        assertNotNull(email.getFromAddress());
        assertEquals("from@example.com", email.getFromAddress().getAddress());
    }

    @Test(expected = EmailException.class)
    public void testSetFromInvalidEmailThrows() throws Exception {
        email.setFrom("invalid-from");
    }

    //  getSentDate() 

    @Test
    public void testGetSentDateDefaultNotNull() {
        Date d = email.getSentDate();
        assertNotNull(d);
    }

    @Test
    public void testGetSentDateReturnsCopy() {
        Date original = new Date(123456789L);
        email.setSentDate(original);

        Date d1 = email.getSentDate();
        Date d2 = email.getSentDate();

        assertEquals(123456789L, d1.getTime());
        assertEquals(123456789L, d2.getTime());
        assertNotSame(d1, d2); // defensive copy check
    }

    //  getSocketConnectionTimeout()

    @Test
    public void testGetSocketConnectionTimeoutDefault() {
        assertEquals(EmailConstants.SOCKET_TIMEOUT_MS, email.getSocketConnectionTimeout());
    }

    @Test
    public void testGetSocketConnectionTimeoutAfterSet() {
        email.setSocketConnectionTimeout(1234);
        assertEquals(1234, email.getSocketConnectionTimeout());
    }

    //  getHostName() 

    @Test
    public void testGetHostNameReturnsConfiguredHost() {
        email.setHostName("smtp.example.com");
        assertEquals("smtp.example.com", email.getHostName());
    }

    @Test
    public void testGetHostNameReturnsNullWhenUnsetAndNoSession() {
        assertNull(email.getHostName());
    }

    // getMailSession() 

    @Test
    public void testGetMailSessionBuildsSessionWhenHostSet() throws Exception {
        email.setHostName("localhost");
        Session s1 = email.getMailSession();
        Session s2 = email.getMailSession();

        assertNotNull(s1);
        assertSame(s1, s2); // cached sesh
        assertEquals("localhost", s1.getProperty(EmailConstants.MAIL_HOST));
    }

    @Test(expected = EmailException.class)
    public void testGetMailSessionThrowsWhenNoHost() throws Exception {
        String old = System.getProperty(EmailConstants.MAIL_HOST);
        try {
            System.clearProperty(EmailConstants.MAIL_HOST);
            new SimpleEmail().getMailSession();
        } finally {
            if (old != null) {
                System.setProperty(EmailConstants.MAIL_HOST, old);
            }
        }
    }

    //  buildMimeMessage() 

    @Test
    public void testBuildMimeMessageSuccess() throws Exception {
        email.setHostName("localhost");
        email.setFrom("from@example.com");
        email.addTo("to@example.com");
        email.setSubject("Unit Test Subject");
        email.setMsg("Body text");
        email.addHeader("X-Test", "yes");

        email.buildMimeMessage();

        assertNotNull(email.getMimeMessage());
        assertEquals("Unit Test Subject", email.getMimeMessage().getSubject());
        assertNotNull(email.getMimeMessage().getSentDate());
    }

    @Test(expected = IllegalStateException.class)
    public void testBuildMimeMessageCalledTwiceThrows() throws Exception {
        email.setHostName("localhost");
        email.setFrom("from@example.com");
        email.addTo("to@example.com");
        email.setMsg("Body text");

        email.buildMimeMessage();
        email.buildMimeMessage(); // second call fails
    }

    @Test(expected = EmailException.class)
    public void testBuildMimeMessageMissingFromThrows() throws Exception {
        email.setHostName("localhost");
        email.addTo("to@example.com");
        email.setMsg("Body text");
        email.buildMimeMessage();
    }

    @Test(expected = EmailException.class)
    public void testBuildMimeMessageMissingRecipientThrows() throws Exception {
        email.setHostName("localhost");
        email.setFrom("from@example.com");
        email.setMsg("Body text");
        email.buildMimeMessage();
    }
}