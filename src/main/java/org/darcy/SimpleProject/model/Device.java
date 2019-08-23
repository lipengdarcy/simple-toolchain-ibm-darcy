package org.darcy.SimpleProject.model;

import javax.persistence.Id;

import lombok.Data;

public @Data class Device {

	@Id
	private Integer id;

	private String deviceName;

}
