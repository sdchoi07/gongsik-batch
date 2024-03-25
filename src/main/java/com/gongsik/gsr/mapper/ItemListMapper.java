package com.gongsik.gsr.mapper;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import com.gongsik.gsr.dto.ItemListDto;

public class ItemListMapper implements FieldSetMapper<ItemListDto> {
    public ItemListDto mapFieldSet(FieldSet fieldSet) {
    	ItemListDto itemListDto = new ItemListDto();

    	itemListDto.setInvenLClsNo(fieldSet.readString(0));
    	itemListDto.setInvenMClsNo(fieldSet.readString(1));
    	itemListDto.setInvenSClsNo(fieldSet.readString(2));
    	itemListDto.setInvenLClsNm(fieldSet.readString(3));
    	itemListDto.setInvenMClsNm(fieldSet.readString(4));
    	itemListDto.setInvenSClsNm(fieldSet.readString(5));
    	itemListDto.setInvenCnt(fieldSet.readInt(5));
    	itemListDto.setInvenPrice(fieldSet.readString(5));

        return itemListDto;
    }
}