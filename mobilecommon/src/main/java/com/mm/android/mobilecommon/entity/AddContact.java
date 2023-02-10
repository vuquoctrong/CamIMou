package com.mm.android.mobilecommon.entity;

import java.util.List;

public class AddContact extends DataInfo {

    private String name;
    private List<NumbersBean> numbers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<NumbersBean> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<NumbersBean> numbers) {
        this.numbers = numbers;
    }

	public static class NumbersBean extends DataInfo{

        private String phone;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }
}
