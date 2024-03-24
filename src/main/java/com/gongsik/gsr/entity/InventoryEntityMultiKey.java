package com.gongsik.gsr.entity;

import java.io.Serializable;


import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor		//전체생성자
@NoArgsConstructor		//기본 생성자
@EqualsAndHashCode		//equals, hashCode
@Data
public class InventoryEntityMultiKey implements Serializable {
	
	@Column(name = "INVEN_L_CLS_NO")
    private String invenLClsNo;
	@Column(name = "INVEN_M_CLS_NO")
    private String invenMClsNo;
    @Column(name = "INVEN_S_CLS_NO")
    private String invenSClsNo;
}
