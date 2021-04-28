package com.example.naturescart.helper

import ir.mirrajabi.searchdialog.core.Searchable


class AreaSearchAble(private var city: String) : Searchable {

    override fun getTitle(): String {
        return city
    }
}