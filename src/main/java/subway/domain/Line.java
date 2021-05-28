package subway.domain;


import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "line")
@Entity
public class Line {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "name", nullable = false)
	private String name;

	@OneToMany(mappedBy = "line")
	private List<Station> stations = new ArrayList<>();

	protected  Line(){
		// empty
	}

	public Line(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public List<Station> getStations() {
		return this.stations;
	}

	public void addStation(Station station) {
		if(this.stations.contains(station)){
			return;
		}
		this.stations.add(station);
	}
}
