import component.DummyMailSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import service.UserService;

@Configuration
public class TestAppContext {

    /** Test 서비스 */
    @Bean
    public UserService testService() {
        return new UserServiceTest.TestUserServiceImpl();
    }


    /** 메일 송신자 */
    @Bean
    public MailSender mailSender() {
        MailSender mailSender = new DummyMailSender();
        return mailSender;
    }

}
