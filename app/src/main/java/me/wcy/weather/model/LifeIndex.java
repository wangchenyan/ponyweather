/**
 * 2015-3-26
 */
package me.wcy.weather.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author wcy
 * 
 */
@DatabaseTable(tableName = "LifeIndex")
public class LifeIndex {

	@DatabaseField(generatedId = true)
	int id;

	@DatabaseField
	String title;

	@DatabaseField
	String zs;

	@DatabaseField
	String tipt;

	@DatabaseField
	String des;

	public LifeIndex() {
		super();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getZs() {
		return zs;
	}

	public void setZs(String zs) {
		this.zs = zs;
	}

	public String getTipt() {
		return tipt;
	}

	public void setTipt(String tipt) {
		this.tipt = tipt;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

}
