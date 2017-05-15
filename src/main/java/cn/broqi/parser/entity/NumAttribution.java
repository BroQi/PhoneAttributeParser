package cn.broqi.parser.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_ownershipofland")
public class NumAttribution {

	private String num;
	private String province;
	private String city;
	private String runcomp;
	private String areacode;
	private String postcode;

	public NumAttribution() {
	}

	public NumAttribution(String province, String city) {
		this.province = province;
		this.city = city;
	}

	@Id
	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRuncomp() {
		return runcomp;
	}

	public void setRuncomp(String runcomp) {
		this.runcomp = runcomp;
	}

	public String getAreacode() {
		return areacode;
	}

	public void setAreacode(String areacode) {
		this.areacode = areacode;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
}
