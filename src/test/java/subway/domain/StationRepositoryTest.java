package subway.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StationRepositoryTest {

	@Autowired
	private StationRepository stations;

	@Autowired
	private LineRepository lines;

	@Test
	void saveTest(){
		// given
		String expected = "잠실역";

		// when
		Station actual = stations.save(new Station(expected)); // 영속성, DB 호출

		// then
		assertThat(actual).isNotNull();
		assertThat(actual.getId()).isNotNull();
		assertThat(actual.getName()).isEqualTo(expected);
	}


	@Test
	void findByNameTest(){
		// given
		String expected = "잠실역";
		Station station = new Station(expected); // 비영속
		stations.save(station); // 영속성 ( DB 호출.. id가 IDENTITY라서 )

		// when
		Station actual = stations.findByName(expected); // DB 호출 PK값이 아닌 속성으로 찾기 때문에
		assertThat(actual)
			.isNotNull();
		assertThat(actual.getName())
			.isEqualTo(expected);
	}

	@Test
	void identityTest(){
		Station expected = new Station("잠실역"); // 비영속성
		stations.save(expected); // 영속
		Station actual = stations.findById(expected.getId()).get();
		assertThat(expected == actual).isTrue();
	}

	@Test
	void identityTest_2(){
		Station expected = new Station("잠실역"); // 비영속성
		Station actual = stations.save(expected); // 영속
		assertThat(expected == actual).isTrue();
	}


	@Test
	void updateTest() {
		Station station1 = stations.save(new Station("잠실역")); // 영속성 ( DB 호출, 컨텍스트 저장, INSERT )
		station1.changeName("몽촌토성역"); // 스냅샷이랑 달라짐 ( 1차캐시에만 저장 )
		Station station2 = stations.findByName("몽촌토성역"); // DB쿼리 날림 (ID로 찾는것이 아니기 때문에 기존 모든 쿼리가 flush가 되고, name을 찾음 )
		assertThat(station2).isNotNull();
	}

	@Test
	void saveWithLineTest() {
		Station expected = new Station("잠실역"); // 비영속성
		expected.setLine(new Line("2호선")); // 비영속성, 연관관계 설정
		Station actual = stations.save(expected); // 영속성
		/**
		 *	insert line
		 *	insert station
		 *	응? 내가 원한 쿼리는 위 2개인데 어째서 ...
		 *	insert 하나만 나오지..? << JPA에서 연관관계를 지정하기위해서는 모든 엔티티가 영속상태여야한다. ( line은 비영속상태 )
		 */

		assertThat(actual).isNotNull();
		assertThat(actual.getName()).isEqualTo("잠실역");
		assertThat(actual.getLine().getName()).isEqualTo("2호선");
	}

	@Test
	void saveWithLineTest_FIX() {
		Station expected = new Station("잠실역"); // 비영속성
		expected.setLine(lines.save(new Line("2호선"))); // 비영속성, 연관관계 설정
		Station actual = stations.save(expected); // 영속성
		/**
		 *	insert line
		 *	insert station
		 */

		assertThat(actual).isNotNull();
		assertThat(actual.getName()).isEqualTo("잠실역");
		assertThat(actual.getLine().getName()).isEqualTo("2호선");
	}

	@Test
	void findByNameWithLineTest() {
		Station actual = stations.findByName("교대역"); // db 조회
		assertThat(actual).isNotNull();
		assertThat(actual.getLine().getName()).isEqualTo("3호선"); // lazy이기때문에 이때 Line 조회
		/**
		 * 쿼리가 두번 실행
		 * findByName 할때 한번
		 * getLine, 연관관계 검색할때 한번 << 지연로딩
		 */
	}

	@Test
	void updateWithLineTest() {
		Station expected = stations.findByName("교대역"); // 쿼리 날림
		expected.setLine(lines.save(new Line("2호선"))); //
		stations.flush(); // transaction commit
	}

	@Test
	void removeLineTest() {
		Station expected = stations.findByName("교대역");
		expected.setLine(null);
		stations.flush(); // transaction commit
	}


	@Test
	void findById() {
		Line line = lines.findByName("3호선"); // DB 조회,
		assertThat(line.getStations()).hasSize(1);
	}

	@Test
	void saveLineTestNotSave() {
		Line expected = new Line("2호선"); // 비영속성
		expected.addStation(new Station("잠실역")); // 비영속성의 Station을 line에 매핑
		lines.save(expected); // save 해도 station은 저장안됨 ( station이 관계의 주인이기에 stations에서 save해줘야한다 )
		lines.flush(); // transaction commit
	}

	@Test
	void saveLineTest() {
		Line expected = new Line("2호선"); // 비영속성
		expected.addStation(stations.save(new Station("잠실역"))); // station은 이제 영속성(쿼리 날라감)
		lines.save(expected); // 쿼리 날라감
		lines.flush(); // transaction commit
	}

}
