
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Email {

    protected static final String SMTP_HOST_NAME = "smtp.gmail.com";
    protected static final String SMTP_PORT ="465";
    protected static final String MAIL_FROM_USER = "no-reply@logiwareinc.com";
    protected static final String MAIL_FROM_PASSWORD = "congruence";
    protected static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    private static final String message = "<font color='red' size ='100px'>THANK YOU </font>";
    private static final String subject = "A test from gmail";
    private static final String[] sendTo = {"sks.256@gmail.com"};

    public static void main(String args[]) throws Exception {

        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        File file = new File("C:\\Documents and Settings\\sunil\\Desktop\\query\\Enhance query.txt");
        File file2 = new File("C:\\Documents and Settings\\sunil\\Desktop\\progress.jpg");
        new Email().send(sendTo, subject, message, new File[]{file, file2});
        System.out.println("Sucessfully Sent mail to All Users");
    }

    public void send(String recipients[], String subject, String message) throws MessagingException {
        send(recipients, subject, message);
    }

    public void send(String recipients[], String subject, String content, File attachement) throws MessagingException {
        send(recipients, subject, content, new File[]{attachement});
    }

    public void send(String recipients[], String subject, String content, File[] attachements) throws MessagingException {
        boolean debug = true;

        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.socketFactory.port", SMTP_PORT);
        props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.put("mail.smtp.socketFactory.fallback", "false");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {

                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(MAIL_FROM_USER, MAIL_FROM_PASSWORD);
                    }
                });

        session.setDebug(debug);
        Message msg = new MimeMessage(session);
        //set subject
        msg.setSubject(subject);

        // create the Multipart and add its parts to it
        Multipart mp = new MimeMultipart();
        mp.addBodyPart(getHTMLContent(content));

        // attach the file to the message
        if (attachements != null) {
            for (File file : attachements) {
                mp.addBodyPart(getAttachement(file));
            }
        }

        // add the Multipart to the message
        msg.setContent(mp);
        // set the Date: header
        msg.setSentDate(new Date());

        InternetAddress addressFrom = new InternetAddress(MAIL_FROM_USER);
        msg.setFrom(addressFrom);

        InternetAddress[] addressTo = new InternetAddress[recipients.length];
        for (int i = 0; i < recipients.length; i++) {
            addressTo[i] = new InternetAddress(recipients[i]);
        }
        msg.setRecipients(Message.RecipientType.TO, addressTo);
        Transport.send(msg);
    }

    private MimeBodyPart getHTMLContent(String html) throws MessagingException {
        MimeBodyPart mbp = new MimeBodyPart();
        mbp.setDataHandler(new DataHandler(new HTMLDataSource(html)));
        return mbp;
    }

    private MimeBodyPart getAttachement(File file) throws MessagingException {
        MimeBodyPart mbp = new MimeBodyPart();
        FileDataSource fds = new FileDataSource(file);
        mbp.setDataHandler(new DataHandler(fds));
        mbp.setFileName(fds.getName());
        return mbp;
    }
}

/*
 * Inner class to act as a JAF datasource to send HTML e-mail content
 */
class HTMLDataSource implements DataSource {

    private String html;

    public HTMLDataSource(String htmlString) {
        html = htmlString;
    }

    // Return html string in an InputStream.
    // A new stream must be returned each time.
    public InputStream getInputStream() throws IOException {
        if (html == null) {
            throw new IOException("Null HTML");
        }
        return new ByteArrayInputStream(html.getBytes());
    }

    public OutputStream getOutputStream() throws IOException {
        throw new IOException("This DataHandler cannot write HTML");
    }

    public String getContentType() {
        return "text/html";
    }

    public String getName() {
        return "JAF text/html dataSource to send e-mail only";
    }
}
