package qna.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static qna.domain.QuestionTest.Q1;
import static qna.domain.UserTest.JAVAJIGI;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

	@Autowired
	private UserRepository users;

	private User expected;

	@BeforeEach
	void setUp() {
		expected = users.save(JAVAJIGI);

		assertThat(expected).isNotNull();
	}


	@Test
	@DisplayName("save 테스트")
	void saveTest() {
		// then
		assertAll(() -> {
			assertThat(expected.getId()).isNotNull();
			assertThat(expected.matchUserId("javajigi")).isTrue();
			assertThat(expected.matchPassword("password")).isTrue();
			assertThat(expected.equalsNameAndEmail(JAVAJIGI)).isTrue();
		});
	}

	@Test
	@DisplayName("update 테스트")
	void updateTest() {
		// given
		String expectedName = "sanjigi";

		// when
		expected.setUserId(expectedName);

		// then
		assertThat(users.findById(expected.getId()))
			.isPresent()
			.get()
			.extracting(value -> value.matchUserId(expectedName))
			.isEqualTo(true);
	}

	@Test
	@DisplayName("findById 테스트")
	void findByIdTest() {
		// when
		assertThat(users.findById(expected.getId()))
			.isPresent()
			.get()
			.isSameAs(expected); // then
	}

	@Test
	@DisplayName("user_id 컬럼으로 User 조회 테스트")
	void findByUserIdTest() {
		// when
		assertThat(users.findByUserId(expected.getUserId()))
			.isPresent()
			.get()
			.isSameAs(expected); // then
	}

	@Test
	@DisplayName("삭제 테스트")
	void deleteTest() {
		// when
		users.delete(expected);

		// then
		assertThat(users.findById(expected.getId()))
			.isNotPresent();
	}
}
