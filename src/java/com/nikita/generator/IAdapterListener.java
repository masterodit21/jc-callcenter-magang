/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator;

import com.rkrzmail.nikita.data.Nset;
/**
 * created by 13k.mail@gmail.com
 */
public interface IAdapterListener {
    public Component getViewItem(int row, int col, Component parent, Nset data);
    //public Component getView(int row, Component parent, Nset data);
}
