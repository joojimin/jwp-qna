package subway.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MemberRepositoryTest {

	@Autowired
	private MemberRepository members;

	@Autowired
	private FavoriteRepository favorites;

	@Test
	void save() {
		Member expected = new Member("jason"); // 비영속성
		expected.addFavorite(favorites.save(new Favorite())); // favorite insert, add
		Member actual = members.save(expected); // members insert
		members.flush(); // transaction commit
	}

}
