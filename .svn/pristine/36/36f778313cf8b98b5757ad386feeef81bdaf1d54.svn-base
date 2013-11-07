package org.teiath.data.domain.aggregator;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "aggr_feed_types")
public class FeedType {

	@Id
	@Column(name = "aggr_type_id")
	@SequenceGenerator(name = "aggr_feed_types_seq", sequenceName = "aggr_feed_types_aggr_type_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "aggr_feed_types_seq")
	private Integer id;

	@Column(name = "aggr_descr", length = 100, nullable = false)
	private String description;


	public FeedType() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && this.id != null && obj.getClass() == FeedType.class && this.id
				.equals(((FeedType) obj).getId());
	}
}
