package org.t2health.breathe2relax;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class B2R_SettingsTable {
	@DatabaseField(generatedId = true)
	private Integer id;

	@DatabaseField
	private String key;

	@DatabaseField
	private String value;

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
