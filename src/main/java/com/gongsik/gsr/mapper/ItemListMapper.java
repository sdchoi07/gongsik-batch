package com.gongsik.gsr.mapper;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import com.gongsik.gsr.dto.ItemListDto;

public class ItemListMapper implements FieldSetMapper<ItemListDto> {
    public ItemListDto mapFieldSet(FieldSet fieldSet) {
    	ItemListDto itemListDto = new ItemListDto();

    	itemListDto.setInvenLclsNo(fieldSet.readString(0));
    	itemListDto.setInvenMclsNo(fieldSet.readString(1));
    	itemListDto.setInvenSclsNo(fieldSet.readString(2));
    	itemListDto.setInvenLclsNm(fieldSet.readString(3));
    	itemListDto.setInvenMclsNm(fieldSet.readString(4));
    	itemListDto.setInvenSclsNm(fieldSet.readString(5));
    	itemListDto.setInvenCnt(fieldSet.readInt(5));
    	itemListDto.setInvenPrice(fieldSet.readString(5));

        return itemListDto;
    }
}