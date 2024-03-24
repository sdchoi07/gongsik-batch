package com.gongsik.gsr.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ItemListDto implements Serializable{
	
	private String invenLclsNo;
	private String invenMclsNo;
	private String invenSclsNo;
	private String invenLclsNm;
	private String invenMclsNm;
	private String invenSclsNm;
	private int invenCnt;
	private String invenPrice;
}
