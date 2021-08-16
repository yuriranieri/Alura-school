package br.com.alura.school.enrollment;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import br.com.alura.school.course.Course;
import br.com.alura.school.course.CourseRepository;
import br.com.alura.school.user.User;
import br.com.alura.school.user.UserRepository;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class EnrollmentControllerTest {
    
    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EnrollmentRepository enrollmentRepository;
    
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void should_retrieve_enroll_report() throws Exception {
        var user = new User("ana", "ana@email.com");
        var course = new Course("java-1", "Java OO", "Java and Object Orientation: Encapsulation, Inheritance and Polymorphism.");

        userRepository.save(user);
        courseRepository.save(course);
        enrollmentRepository.save(new Enrollment(LocalDateTime.now(), course, user));

        mockMvc.perform(get("/courses/enroll/report")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].email", is("ana@email.com")))
                .andExpect(jsonPath("$[0].quantidade_matriculas", is(1)));
    }

    @Test
    void should_retrieve_enroll_report_order_by_quantity() throws Exception {
        var userAna = new User("ana", "ana@email.com");
        var userAlex = new User("alex", "alex@email.com");
        var courseJava = new Course("java-1", "Java OO", "Java and Object Orientation: Encapsulation, Inheritance and Polymorphism.");
        var courseSpring = new Course("spring-1", "Spring Boot", "Spring Boot");

        userRepository.save(userAna);
        userRepository.save(userAlex);
        courseRepository.save(courseJava);
        courseRepository.save(courseSpring);
        enrollmentRepository.save(new Enrollment(LocalDateTime.now(), courseJava, userAna));
        enrollmentRepository.save(new Enrollment(LocalDateTime.now(), courseSpring, userAna));
        enrollmentRepository.save(new Enrollment(LocalDateTime.now(), courseJava, userAlex));

        mockMvc.perform(get("/courses/enroll/report")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].email", is("ana@email.com")))
                .andExpect(jsonPath("$[0].quantidade_matriculas", is(2)))
                .andExpect(jsonPath("$[1].email", is("alex@email.com")))
                .andExpect(jsonPath("$[1].quantidade_matriculas", is(1)));
    }

    @Test
    void no_content_when_does_not_exist_user_with_enrollment() throws Exception {
        mockMvc.perform(get("/courses/enroll/report")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void should_add_new_enroll() throws Exception {
        userRepository.save(new User("ana", "ana@email.com"));
        courseRepository.save(new Course("java-1", "Java OO", "Java and Object Orientation: Encapsulation, Inheritance and Polymorphism."));
        NewEnrollmentRequest newEnrollmentRequest = new NewEnrollmentRequest("ana");

        mockMvc.perform(post("/courses/java-1/enroll")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(newEnrollmentRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void should_not_allow_duplication_of_user() throws Exception {
        var user = new User("ana", "ana@email.com");
        var course = new Course("java-1", "Java OO", "Java and Object Orientation: Encapsulation, Inheritance and Polymorphism.");

        userRepository.save(user);
        courseRepository.save(course);
        enrollmentRepository.save(new Enrollment(LocalDateTime.now(), course, user));

        NewEnrollmentRequest newEnrollmentRequest = new NewEnrollmentRequest("ana");

        mockMvc.perform(post("/courses/java-1/enroll")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(newEnrollmentRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_not_enroll_when_course_does_not_exist() throws Exception {
        userRepository.save(new User("ana", "ana@email.com"));

        NewEnrollmentRequest newEnrollmentRequest = new NewEnrollmentRequest("ana");

        mockMvc.perform(post("/courses/java-1/enroll")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(newEnrollmentRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void should_not_enroll_when_user_does_not_exist() throws Exception {
        courseRepository.save(new Course("java-1", "Java OO", "Java and Object Orientation: Encapsulation, Inheritance and Polymorphism."));
        NewEnrollmentRequest newEnrollmentRequest = new NewEnrollmentRequest("ana");

        mockMvc.perform(post("/courses/java-1/enroll")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(newEnrollmentRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @CsvSource({
            "''",
            "'    '",
            "an-username-that-is-really-really-big"
    })
    void should_validate_bad_enroll_requests(String username) throws Exception {
        NewEnrollmentRequest newEnroll = new NewEnrollmentRequest(username);

        mockMvc.perform(post("/courses/java-1/enroll")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(newEnroll)))
                .andExpect(status().isBadRequest());
    }
}
