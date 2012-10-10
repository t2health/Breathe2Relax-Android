package org.t2health.breathe2relax;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class B2R_MoodTrackingsTable {
	@DatabaseField(generatedId = true)
	private Integer id;

	@DatabaseField
	private Date date;

	@DatabaseField
	private Integer beforeResult;

	@DatabaseField
	private Integer afterResult;

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

	public void setBeforeResult(Integer beforeResult) {
		this.beforeResult = beforeResult;
	}

	public Integer getBeforeResult() {
		return beforeResult;
	}

	public void setAfterResult(Integer afterResult) {
		this.afterResult = afterResult;
	}

	public Integer getAfterResult() {
		return afterResult;
	}
}
