package com.hollysmart.formmodule.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class FormDictionaryBean implements Serializable {


    private Map<String, List<DictionaryBean>> dictOptions;

}
