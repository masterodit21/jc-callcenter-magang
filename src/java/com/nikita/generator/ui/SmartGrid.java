/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nikita.generator.ui;

import com.nikita.generator.Component;
import static com.nikita.generator.Component.escapeHtml;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaViewV3;
import com.nikita.generator.Style;
import com.nikita.generator.ui.layout.DivLayout;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.util.Vector;

public class SmartGrid extends Component {

    private OnFilterClickListener listener;

    public void setOnFilterClickListener(OnFilterClickListener listener) {
        setOnFilterClickListener(listener, 1, 10);
    }

    public void setOnFilterClickListener(OnFilterClickListener listener, int curpage, int rowperpage) {
        setCurrentPage(curpage);//first
        rowperPage = rowperpage;//def
        this.listener = listener;
    }

    public OnFilterClickListener getOnFilterClickListener() {
        return listener;
    }

    public interface OnFilterClickListener {

        public void OnFilter(NikitaRequest request, NikitaResponse response, Component component);
    }

    /*
     public interface OnItemViewListener {
        public NikitaResponse OnItemView(Component component);
    }  
    protected OnItemViewListener itemviewListener;
    public void setOnItemViewListener(OnItemViewListener listener){
        this.itemviewListener=listener;
    }
    
    protected OnItemViewListener getOnItemViewListener(){
        return itemviewListener;
    }
     */

    private int getDataSize() {
        if (Nikitaset.isNikitaset(getData())) {
            return getData().getData("data").getArraySize();
        } else if (getData() != null) {
            return getData().getSize();
        }
        return 0;
    }

    private String getDataText(int row, int col) {
        return "";
    }

    public String getText() {
        Nset n = Nset.newObject();
        n.setData("selected", bufferedata);
        n.setData("currentdata", currtext);
        return n.toJSON();
    }

    public void clearBufferData() {
        bufferedata = Nset.newArray();
    }

    public void setSelectedRow(String arrayjson) {
        if (arrayjson.trim().startsWith("[") && arrayjson.trim().endsWith("]")) {
            bufferedata = Nset.readJSON(arrayjson);
        }
    }

    public Style getStyle() {
        return super.getStyle() != null ? super.getStyle() : Style.createStyle();
    }

    private String getHeaderText(Nikitaset data, Nset nHeader, int col) {
        StringBuffer sb = new StringBuffer();
        if (nHeader.getSize() <= col) {
            sb.append(data.getHeader(col));
        } else if (nHeader.getData(col).isNsetObject()) {
            if (nHeader.getData(col).getData("text").toString().equals("*")) {
                sb.append(data.getHeader(col));
            } else {
                sb.append(nHeader.getData(col).getData("text").toString());
            }
        } else if (nHeader.getData(col).isNsetArray()) {
            if (nHeader.getData(col).getData(0).toString().equals("*")) {
                sb.append(data.getHeader(col));
            } else {
                sb.append(nHeader.getData(col).getData(0).toString());
            }
        } else if (nHeader.getData(col).toString().equals("*")) {
            sb.append(data.getHeader(col));
        } else if (nHeader.getSize() > col) {
            sb.append(nHeader.getData(col).toString());
        } else {
            sb.append(data.getHeader(col));
        }
        return sb.toString();
    }
    private Nset hideColEmpty = Nset.newObject();
    private boolean sendclicked = true;
    private boolean senditemClicked = true;

    public String getView(NikitaViewV3 v3) {
        v3.setData(this);
        StringBuilder sb = new StringBuilder();
        sb.append(nDivView("nikitasmargrid"));//sb.append("<div ").append(getStyle()!=null?getStyle().getViewAttr("n-div-", " "):"").append(" style=\"").append(isVisible()?"":"display:none;").append(getViewStyle()).append("\" ").append(getViewAttribut()).append("   title=\"").append( escapeHtml(getTooltip()) ).append("\"  class=\"component ").append(getVisibleEnable()).append("").append(getFormJsId()).append("  \"  id=\"").append(getJsId()).append("\" nid=\"").append(getId()).append("\"  nform=\"").append(getFormId()).append("\"  ntype=\"nikitasmargrid\" >\n");
        sb.append(getTagView());
        sb.append(getLabelView());
        sb.append("<div style=\"display:none\" id=\"").append(SmartGrid.this.getJsId() + "_buffer").append("\"  >").append(escapeHtml(bufferedata.toJSON())).append("</div>");
        if (getStyle().getInternalStyle().getData("n-hide-paging-up").toString().equals("true")) {
        } else {
            sb.append(getViewFilterPage(false, v3));
        }
        sb.append("<div class=\"table-responsive\">");
        sb.append("<table id=\"").append(getJsId()).append("_table\" width=\"100%\"  ").append(Style.createStyle(getStyle()).setAttrIfNoExist("n-table-border", "1px").setAttrIfNoExist("n-table-cellpadding", "3px").setAttrIfNoExist("n-table-cellspacing", "0").getViewAttrPrefix("n-table-")).append("  class=\"table table-striped table-hover\">");
        //create header

        Nset nHeader = Nset.newArray();
        if (header != null && !header.equals("")) {
            if (header.trim().startsWith("{") || header.trim().startsWith("[")) {
                nHeader = Nset.readJSON(header);
            } else {
                nHeader = Nset.readsplitString(header, ",");
            }
        }

        hideColEmpty = Nset.newObject();
        sendclicked = getStyle().getViewStyle().contains("n-clickaction:false") ? false : true;//def true
        senditemClicked = getStyle().getViewStyle().contains("n-filteronly:true") ? false : true;//def false

        sb.append("<thead class=\"smartgrid-headers\">");
        if (Nikitaset.isNikitaset(getData())) {
            Nikitaset data = new Nikitaset(getData());
            sb.append("<tr  id=\"").append(getJsId()).append("-header\" ncols=\"").append(data.getCols()).append("\"  nrow=\"").append(0).append("\"  class=\"background: #579098;\n"
                    + "border-radius: 6px;\"  >");

            sb.append(colomRowNum(-1, ""));
            for (int col = 0; col < data.getCols(); col++) {

                /*
                        sb.append("<th class=\"smartgridresizable \" >");
                     
                        if (nHeader.getSize()>=1) {
                            if (nHeader.getData(col).isNsetObject() ){
                                if (nHeader.getData(col).getData("text").toString().equals("")) {
                                    sb.append(data.getHeader(col));//* hide
                                }else{
                                    sb.append(nHeader.getData(col).getData("text").toString());
                                }
                            } else  if (nHeader.getData(col).isNsetArray() ){
                                if (nHeader.getData(col).getData(0).toString().equals("")) {
                                    sb.append(data.getHeader(col));//*  hide
                                }else{
                                    sb.append(nHeader.getData(col).getData(0).toString());
                                }
                            } else  if ( nHeader.getData(col).toString().equals("") ){
                                sb.append(data.getHeader(col));//*  hide
                            } else  if ( nHeader.getData(col).toString().length() >= 1 ){
                                sb.append(nHeader.getData(col).toString());
                            }else{
                                sb.append(data.getHeader(col));
                            }                            
                        }else{
                            sb.append(data.getHeader(col));
                        }     
                 */
                String hrd = getHeaderText(data, nHeader, col);
                if (hrd.equals("") && hideheaderempty) {
                    hideColEmpty.setData("" + col, "true");
                    sb.append("<th class=\"smartgridresizable smartgridcolhide\" >");
                } else {
                    sb.append("<th class=\"smartgridresizable \" >");
                    sb.append(hrd);
                }

                sb.append(colomSort(col));
                sb.append("</th>");
            }
            sb.append(colomRowAction(-1, "[]"));
        } else {
            sb.append("<tr  class=\"\">");

        }
        sb.append("</tr>");
        sb.append("</thead>");

        Nset nDataView = Nset.newArray();
        if (dataview != null && !dataview.equals("")) {
            if (dataview.trim().startsWith("{") || dataview.trim().startsWith("[")) {
                nDataView = Nset.readJSON(dataview);
            } else {
                nDataView = Nset.readsplitString(dataview, ",");
            }
        }

        //loop content
        sb.append("<tbody>");
        if (Nikitaset.isNikitaset(getData())) {
            Nikitaset data = new Nikitaset(getData());
            for (int row = 0; row < data.getRows(); row++) {

                //add 02/01/22
                boolean enablesdc = true;
                String gmasterBG = "gmaster";
                String sdc = getStyle().getInternalStyle().getData("deskcoll").toString();
                if (sdc.equalsIgnoreCase("true")) {
                    if (data.getText(row, 3).startsWith("T")) {
                        gmasterBG = "bgcolor=\"yellow\"";
                    } else {
                        enablesdc = false;
                    }
                }

                sb.append("<tr ").append(gmasterBG).append(" class=\"\"  style=\" ").append(getOnClickListener() != null && senditemClicked ? "cursor: pointer; " : "").append(" ").append(getStyle().getViewAttrPrefix("n-tbody-tr-")).append("\"  ").append(getStyle().getViewAttrPrefix("n-tbody-tr-")).append(" >"); //datagrid-header-row 
                Component[] components = null;
                setCurrentDetail(getDefaultDetailView());
                if (smartGridItemViewListener != null) {
                    components = smartGridItemViewListener.onItemView(this, row, data);
                }
                int ifactor = ((getCurrentPage() - 1) * getShowPerPage());
                String checkid = (row + 1) + (ifactor <= 0 ? 0 : ifactor) + "";
                if (colcheckbox != null && !colcheckbox.equals("")) {
                    if (Utility.isNumeric(colcheckbox)) {
                        checkid = data.getText(row, Utility.getInt(colcheckbox));
                    } else {
                        checkid = data.getText(row, colcheckbox);
                    }
                }

                sb.append(colomRowNum(row, checkid));

                for (int col = 0; col < data.getCols(); col++) {
                    if (components != null && components.length > col && components[col] != null) {
                        if (components[col].getId().equals(Component.COMPONENTIDNONUI)) {
                            sb.append(colomRowDefault(row, col, data.getText(row, col), null));
                        } else if (components[col] instanceof Function) {
                            // sb.append(  );

                        } else if (components[col] instanceof NikitaForm) {
                            sb.append(components[col].getView());
                        } else {
                            sb.append(components[col].getView());
                        }
                    } else if (nDataView.getSize() >= 1) {
                        String comptype = "";
                        if (nDataView.getData(col).isNsetObject()) {
                            comptype = nDataView.getData(col).getData("text").toString();
                        } else {
                            comptype = nDataView.getData(col).toString();
                        }

                        if (comptype.equals("") || comptype.equals("label")) {
                            Style style = Style.createStyle(nDataView.getData(col).getData("style").toString());
                            String text = getMatrixText(style, data.getText(row, col));
                            if (style.getInternalAttr().containsKey("textview")) {
                                if (gridTextViewListener != null) {
                                    text = gridTextViewListener.onTextView(style.getInternalAttr().getData("textview").toString(), row, col, text);
                                }
                            }
                            sb.append(colomRowDefault(row, col, text, style));
                        } else if (comptype.equals("button")) {
                            Button button = new Button() {
                                public String getClickAction(String action) {
                                    if (isEnable() && isVisible()) {
                                        return SmartGrid.this.getFilterAction("actioncell-" + row + "-" + (col) + "-button", row);
                                    }
                                    return "";
                                }
                                int col;
                                int row;

                                public Button get(int row, int col) {
                                    this.col = col;
                                    this.row = row;
                                    return this;
                                }
                            }.get(row, col);
                            Style style = Style.createStyle(nDataView.getData(col).getData("style").toString());
                            button.setStyle(style);
                            button.setText(getMatrixText(style, data.getText(row, col)));
                            sb.append("<td class=\"item ").append(hideColEmpty.containsKey("" + col) ? "smartgridcolhide" : "").append("\" style=\"").append(getStyle().getViewStylePrefix("n-tbody-td-" + col + "-")).append("  ").append(style != null ? style.getViewStyle() : "").append(" \"  ").append(getStyle().getViewAttrPrefix("n-tbody-td-" + col + "-")).append(" ").append(style != null ? style.getViewAttr() : "").append("    >");
                            //sb.append("<td class=\"item ").append(hideColEmpty.containsKey(""+col)?"smartgridcolhide":"").append("\" >");
                            sb.append(button.getView(v3));
                            sb.append("</td>");
                        } else if (comptype.equals("3button") || comptype.equals("2button")) {
                            HorizontalLayout hl = new HorizontalLayout();
                            Style style = Style.createStyle(nDataView.getData(col).getData("style").toString());

                            for (int i = 0; i < (comptype.equals("3button") ? 3 : 2); i++) {

                                Button button = new Button() {
                                    public String getClickAction(String action) {
                                        if (isEnable() && isVisible()) {
                                            return SmartGrid.this.getFilterAction("actioncell-" + row + "-" + (col) + "-button" + (but + 1), row);
                                        }
                                        return "";
                                    }
                                    int col;
                                    int but;
                                    int row;

                                    public Button get(int row, int col, int but) {
                                        this.col = col;
                                        this.but = but;
                                        this.row = row;
                                        return this;
                                    }
                                }.get(row, col, i);
                                button.setEnable(enablesdc);
                                hl.addComponent(button);
                                button.setStyle(style);
                                if (style.getInternalStyle().getData("n-button" + (i + 1)).toString().equals("")) {
                                    button.setText(getMatrixText(style, data.getText(row, col)));
                                } else {
                                    button.setText(style.getInternalStyle().getData("n-button" + (i + 1)).toString());
                                }

                            }
                            sb.append("<td class=\"item ").append(hideColEmpty.containsKey("" + col) ? "smartgridcolhide" : "").append("\" style=\"").append(getStyle().getViewStylePrefix("n-tbody-td-" + col + "-")).append("  ").append(style != null ? style.getViewStyle() : "").append(" \"  ").append(getStyle().getViewAttrPrefix("n-tbody-td-" + col + "-")).append(" ").append(style != null ? style.getViewAttr() : "").append("    >");
                            //sb.append("<td class=\"item ").append(hideColEmpty.containsKey(""+col)?"smartgridcolhide":"").append("\"  >");
                            sb.append(hl.getView(v3));
                            sb.append("</td>");
                        } else if (comptype.equals("image")) {
                            Image button = new Image() {
                                public String getClickAction(String action) {
                                    if (isEnable() && isVisible()) {
                                        return SmartGrid.this.getFilterAction("actioncell-" + row + "-" + (col) + "-image", row);
                                    }
                                    return "";
                                }
                                int col;
                                int row;

                                public Image get(int row, int col) {
                                    this.col = col;
                                    this.row = row;
                                    return this;
                                }
                            }.get(row, col);

                            Style style = Style.createStyle(nDataView.getData(col).getData("style").toString());
                            button.setStyle(style);
                            button.setText(style.getInternalStyle().getData("n-prefix").toString() + getMatrixText(style, data.getText(row, col)));
                            sb.append("<td class=\"item ").append(hideColEmpty.containsKey("" + col) ? "smartgridcolhide" : "").append("\" style=\"").append(getStyle().getViewStylePrefix("n-tbody-td-" + col + "-")).append("  ").append(style != null ? style.getViewStyle() : "").append(" \"  ").append(getStyle().getViewAttrPrefix("n-tbody-td-" + col + "-")).append(" ").append(style != null ? style.getViewAttr() : "").append("    >");
                            //sb.append("<td class=\"item ").append(hideColEmpty.containsKey(""+col)?"smartgridcolhide":"").append("\"  >");
                            sb.append(button.getView(v3));
                            sb.append("</td>");
                        } else {
                            Style style = Style.createStyle(nDataView.getData(col).getData("style").toString());
                            sb.append(colomRowDefault(row, col, getMatrixText(style, data.getText(row, col)), style));
                        }
                    } else {
                        //just show
                        Style style = Style.createStyle(nDataView.getData(col).getData("style").toString());
                        sb.append(colomRowDefault(row, col, getMatrixText(style, data.getText(row, col)), style));
                    }
                }

                sb.append(colomRowAction(row, curraction == null ? defaction : (curraction.equals("") ? defaction : curraction)));
                curraction = defaction;
                sb.append("</tr>");

                //master detail View                     
                if (smartGridDetailViewListener != null) {

                    NikitaForm form = smartGridDetailViewListener.onDetailView(this, row, data);
                    if (form != null) {
                        //master detail
                        sb.append("<tr ").append(getStyle().getViewAttrPrefix("n-detail")).append(">");
                        sb.append("<td colspan=\"").append(data.getCols() + 2).append("\" style=\"background-color:transparent\"> ");
                        ///  style=\"").append(getStyle().getViewAttrPrefix("n-tbody-td-"+col+"-")).append("\"  ").append(getStyle().getViewAttrPrefix("n-tbody-td-"+col+"-")).append(" 
                        sb.append(form.getViewForm(v3));

                        sb.append("</td>");
                        sb.append("</tr>");
                    } else {
                        sb.append("<tr class=\"smartgriddetailviewnone\" ").append(getStyle().getViewAttrPrefix("n-detail")).append(">");
                        sb.append("<td colspan=\"").append(data.getCols() + 2).append("\" style=\"background-color:transparent\"> ");
                        sb.append("</td>");
                        sb.append("</tr>");
                    }
                }

                curradetail = "";
            }

        } else {
        }
        sb.append("</tbody>");
        sb.append("</table>");
        sb.append("</div>");
        sb.append(getViewFilterPage(true, v3));
        sb.append("</div>");
        return sb.toString();
    }

    private String getMatrixText(Style style, String text) {
        Nset styleCol = style.getInternalStyle();
        if (styleCol.containsKey("n-format")) {
            String format = styleCol.getData("n-format").toString();
            if (format.equalsIgnoreCase("curr") || format.equalsIgnoreCase("currency")) {
                text = Utility.formatCurrency(text);
            } else if (format.equalsIgnoreCase("fdateview")) {
                text = Utility.formatDate(Utility.getDate(text), "dd/MM/yyyy");
            } else if (format.equalsIgnoreCase("fdatetimeview")) {
                text = Utility.formatDate(Utility.getDateTime(text), "dd/MM/yyyy HH:mm:ss");
            } else if (format.equalsIgnoreCase("fdatedb")) {
                text = Utility.formatDate(Utility.getDate(text), "yyyy-MM-dd");
            } else if (format.equalsIgnoreCase("fdatetimedb")) {
                text = Utility.formatDate(Utility.getDateTime(text), "yyy-MM-dd HH:mm:ss");
            } else if (format.equalsIgnoreCase("fdate")) {
                text = Utility.formatDate(Utility.getDateTime(text), styleCol.getData("n-format-text").toString());
            } else if (format.equalsIgnoreCase("number")) {
                if (Utility.isLongIntegerNumber(text)) {
                    text = Utility.getNumber(text).longValue() + "";
                } else {
                    text = Utility.getNumber(text).doubleValue() + "";
                }
            } else if (format.equalsIgnoreCase("int") || format.equalsIgnoreCase("integer")) {
                text = Utility.getNumber(text).intValue() + "";
            } else if (format.equalsIgnoreCase("long")) {
                text = Utility.getNumber(text).longValue() + "";
            } else if (format.equalsIgnoreCase("double")) {
                text = Utility.getNumber(text).doubleValue() + "";
            } else if (format.equalsIgnoreCase("numberonly")) {
                text = Utility.getNumberOnly(text);
            } else if (format.equalsIgnoreCase("ucase")) {
                text = text.toUpperCase();
            } else if (format.equalsIgnoreCase("lcase")) {
                text = text.toLowerCase();
            }
        }
        return text;
    }
    private boolean showrowmaster = true;

    public void showRowMaster(boolean showrowmaster) {
        this.showrowmaster = showrowmaster;
    }

    private boolean showrownum = false;

    public void showRowNumber(boolean showrownum) {
        this.showrownum = showrownum;
    }
    private String defaction = "";
    private boolean showaction = false;

    public void showAction(boolean showaction) {
        this.showaction = showaction;
    }

    public void showAction(boolean showaction, String defaction) {
        this.defaction = defaction;
        showAction(showaction);
    }
    private boolean sortable;

    public void sortableGrid(boolean sortable) {
        this.sortable = sortable;
    }
    private boolean showcheckbox = false;
    private String colcheckbox = "";
    private Nset bufferedata = Nset.newArray();
    private Nset bufferesort = Nset.newObject();

    public void showRowCheckbox(boolean showcheckbox, String colomid) {
        this.showcheckbox = showcheckbox;
        this.colcheckbox = colomid;
    }
    private String header;

    public void setDefaultHeader(String header) {
        this.header = header;
    }
    private String dataview;

    public void setDefaultDataView(String dataview) {
        this.dataview = dataview;
    }
    private String detailview;

    public void setDefaultDetailView(String detailview) {
        this.detailview = detailview;
    }

    public String getDefaultDetailView() {
        return this.detailview;
    }

    private boolean hideheaderempty;

    public void setHideHeaderEmpty(boolean hide) {
        this.hideheaderempty = hide;
    }

    int row;

    public int getItemRow() {
        return row;
    }

    public void setItemRow(int row) {
        this.row = row;
    }

    private Nset getSelectedId() {
        return Nset.newArray();
    }

    private String colomRowDefault(int row, int col, String data, Style style) {
        StringBuffer sb = new StringBuffer();

        sb.append("<td id=\"").append(getJsId()).append("-cell-").append(row + "-" + col).append("\" class=\"item ").append(hideColEmpty.containsKey("" + col) ? "smartgridcolhide" : "").append(" ").append(style != null ? style.getViewClass() : "").append("  \"  ").append(getClickAction("cellselect-" + row + "-" + col, row)).append("   style=\"").append(getStyle().getViewStylePrefix("n-tbody-td-" + col + "-")).append("  ").append(style != null ? style.getViewStyle() : "").append(" \"  ").append(getStyle().getViewAttrPrefix("n-tbody-td-" + col + "-")).append(" ").append(style != null ? style.getViewAttr() : "").append("    >");
        sb.append(hideColEmpty.containsKey("" + col) ? data : data);//tetap muncul
        sb.append("</td>");

        return sb.toString();
    }

    private String colomSort(int col) {
        StringBuffer sb = new StringBuffer();
        sb.append("<div >");
        if (sortable) {
            if (actionSort.equals("page-sort-" + col + "-desc")) {
                sb.append("<div name=\"").append(getJsId()).append("_sort\" nvalue=\"").append(col).append("\" nsort=\"desc\" class=\"smartsorticon-desc\" ").append(getFilterAction("page-sort-" + col + "-asc", -1)).append("></div>");
            } else if (actionSort.equals("page-sort-" + col + "-asc")) {
                sb.append("<div name=\"").append(getJsId()).append("_sort\" nvalue=\"").append(col).append("\" nsort=\"asc\" class=\"smartsorticon-asc\" ").append(getFilterAction("page-sort-" + col + "-desc", -1)).append("></div>");
                //}else  if (bufferesort.getData(col+"").toString().equals("desc")) {
                //sb.append("<div name=\"").append(getJsId()).append("_sort\" nvalue=\"").append(col).append("\" nsort=\"desc\" class=\"smartsorticon-desc\" ").append( getFilterAction("page-sort-"+col+"-asc") ).append("></div>");
                //}else if (bufferesort.getData(col+"").toString().equals("asc")) {
                //sb.append("<div name=\"").append(getJsId()).append("_sort\" nvalue=\"").append(col).append("\" nsort=\"asc\" class=\"smartsorticon-asc\" ").append( getFilterAction("page-sort-"+col+"-desc") ).append("></div>");
            } else {
                sb.append("<div name=\"").append(getJsId()).append("_sort\" nvalue=\"").append(col).append("\" nsort=\"\" class=\"smartsorticon\" ").append(getFilterAction("page-sort-" + col + "-asc", -1)).append("></div>");
            }
        }
        sb.append("</div>");

        return sb.toString();
    }

    private String colomRowNum(int row, String id) {
        StringBuffer sb = new StringBuffer();
        if (showrownum) {
            if (row == -1) {
                sb.append("<th class=\"smartgridrownum-header\">");
                sb.append("");
                sb.append("</th>");
            } else {
                sb.append("<td class=\"smartgridrownum\">");
                sb.append("<table style=\"width:100%;\" ><tr><td style=\"text-align:right\">");
                int ifactor = ((getCurrentPage() - 1) * getShowPerPage());
                String rowid = (row + 1) + (ifactor <= 0 ? 0 : ifactor) + "";
                sb.append(rowid);
                sb.append("</td>");
                if (showcheckbox) {
                    sb.append("<td class=\"smartgridrownum-check\">");
                    sb.append("<input type=\"checkbox\" nvalue=\"").append(escapeHtml(id)).append("\" ").append(bufferedata.containsValue(id) ? "checked" : "").append(" name=\"").append(getJsId()).append("_text\" value=\"").append("").append("\" ").append(sendclicked ? getClickAction("select-" + row, row) : "").append("  >").append("").append("</input>   \n");
                    sb.append("</td>");
                }
                sb.append("</tr></table>");
                sb.append("</td>");
            }
        } else if (showcheckbox) {
            if (row == -1) {
                sb.append("<th class=\"smartgridrownum-header\">");
                sb.append("");
                sb.append("</th>");
            } else {
                sb.append("<td class=\"smartgridrownum-check\">");
                sb.append("<input type=\"checkbox\" nvalue=\"").append(escapeHtml(id)).append("\" ").append(bufferedata.containsValue(id) ? "checked" : "").append(" name=\"").append(getJsId()).append("_text\" value=\"").append("").append("\" ").append(sendclicked ? getClickAction("select-" + row, row) : "").append("  >").append("").append("</input>   \n");
                sb.append("</td>");
            }
        } else {
            if (row == -1) {
                sb.append("<th style=\"display:none\">");
                sb.append("</th>");
            } else {
                sb.append("<td style=\"display:none\">");
                sb.append("</td>");
            }

        }
        return sb.toString();
    }

    private String colomRowAction(int row, String data) {
        //[edit,add,view...] -- [{"text":"edit","action":"edit","visible":"1","enable":"1","type":"image"},..]
        StringBuffer sb = new StringBuffer();

        Nset alldata = Nset.readJSON(data);
        if (showaction) {
            if (row == -1) {
                sb.append("<th style=\"width:10px\"  class=\"smartgridaction-header\">");
                sb.append("");//action
                sb.append("</th>");
            } else {
                sb.append("<td class=\"smartgridaction\">");
                HorizontalLayout layout = new HorizontalLayout();
                for (int i = 0; i < alldata.getArraySize(); i++) {
                    Nset vdata = alldata.getData(i);
                    Component component;
                    if (vdata.isNsetObject()) {
                        Style style = Style.createStyle(vdata.getData("custom").toString());
                        if (vdata.getData("visible").toString().equalsIgnoreCase("custom")) {
                            if (style.getInternalStyle().containsKey("n-visible")) {
                                vdata.setData("visible", style.getInternalStyle().getData("n-visible").toString());
                            } else {
                                vdata.setData("visible", "visible");
                            }

                            if (style.getInternalStyle().containsKey("n-action")) {
                                vdata.setData("action", style.getInternalStyle().getData("n-action").toString());
                            }
                            if (style.getInternalStyle().containsKey("n-text")) {
                                vdata.setData("text", style.getInternalStyle().getData("n-text").toString());
                            }
                        }

                        if (style.getInternalStyle().getData("type").toString().equals("n-menu")) {
                            //new model
                            component = new Image() {
                                public String getClickAction(String action) {
                                    if (isEnable() && isVisible()) {
                                        //return SmartGrid.this.getFilterAction("actiongrid-"+row+"-"+curraction, row);
                                    }
                                    return "";
                                }

                                public void setText(String text) {
                                    if (text.contains("/")) {
                                        super.setText(Component.getBaseUrl(text));
                                    } else {
                                        super.setText(Component.getBaseUrl("/static/img/menu.png"));
                                    }
                                }
                                private int row = 0;
                                private String curraction = "";

                                public Image get(int row, String action) {
                                    this.row = row;
                                    this.curraction = action;
                                    return this;
                                }
                            }.get(row, vdata.getData("action").toString().equals("") ? vdata.getData("text").toString() : vdata.getData("action").toString());
                            //component.setStyle(new Style().addClass("n-menu-smartgrid-action").setAttr("items", style.getInternalAttr().getData("items").toString()));

                            Nset n = Nset.newArray();
                            Vector vItems = Utility.splitVector(style.getInternalAttr().getData("items").toString(), ",");
                            for (int j = 0; j < vItems.size(); j++) {
                                String item = String.valueOf(vItems.elementAt(j));
                                if (item.contains("-")) {
                                    String[] itemdetail = Utility.split(item, "-");
                                    //key:name:icon
                                    if (itemdetail.length >= 3) {
                                        n.addData(Nset.newObject().setData("key", itemdetail[0]).setData("name", itemdetail[1]).setData("icon", itemdetail[2]));
                                    } else {
                                        n.addData(Nset.newObject().setData("key", itemdetail[0]).setData("name", itemdetail[1]));
                                    }
                                } else {
                                    n.addData(Nset.newObject().setData("key", item).setData("name", item));
                                }

                            }
                            component.setStyle(new Style().addClass("n-menu-smartgrid-action").setAttr("items", Utility.encodeBase64(n.toJSON())).setAttr("item-action", Utility.encodeBase64(Nset.newObject().setData("formid", SmartGrid.this.getFormJsId()).setData("compid", SmartGrid.this.getJsId()).setData("row", row).setData("action", "actiongrid-" + row + "-menu-").toJSON())));

                            component.setTooltip(style.getInternalAttr().getData("tooltip").toString());
                        } else if (vdata.getData("type").toString().equals("") || vdata.getData("type").toString().equals("img") || vdata.getData("type").toString().equals("image")) {
                            component = new Image() {
                                public String getClickAction(String action) {
                                    if (isEnable() && isVisible()) {
                                        return SmartGrid.this.getFilterAction("actiongrid-" + row + "-" + curraction, row);
                                    }
                                    return "";
                                }

                                public void setText(String text) {
                                    if (text.contains("/")) {
                                        super.setText(Component.getBaseUrl(text));
                                    } else {
                                        super.setText(Component.getBaseUrl("/static/img/" + text + ".png"));
                                    }
                                }
                                private int row = 0;
                                private String curraction = "";

                                public Image get(int row, String action) {
                                    this.row = row;
                                    this.curraction = action;
                                    return this;
                                }

                            }.get(row, vdata.getData("action").toString().equals("") ? vdata.getData("text").toString() : vdata.getData("action").toString());
                            component.setTooltip(style.getInternalAttr().getData("tooltip").toString());

                        } else if (vdata.getData("type").toString().equals("button")) {
                            component = new Button() {
                                public String getClickAction(String action) {
                                    if (isEnable() && isVisible()) {
                                        return SmartGrid.this.getFilterAction("actiongrid-" + row + "-" + curraction, row);
                                    }
                                    return "";
                                }
                                private int row = 0;
                                private String curraction = "";

                                public Button get(int row, String action) {
                                    this.row = row;
                                    this.curraction = action;
                                    return this;
                                }
                            }.get(row, vdata.getData("action").toString().equals("") ? vdata.getData("text").toString() : vdata.getData("action").toString());
                        } else if (vdata.getData("type").toString().equals("link") || vdata.getData("type").toString().equals("href") || vdata.getData("type").toString().equals("url")) {
                            component = new Label() {
                                public String getView() {
                                    StringBuffer sb = new StringBuffer();
                                    sb.append("<a href=\"\"").append(Component.getBaseUrl(getData().getData("action").toString().equals("") ? getData().getData("text").toString() : getData().getData("action").toString())).append(">");
                                    sb.append(getText());
                                    sb.append("</a>");
                                    return sb.toString();
                                }
                            };
                        } else {
                            component = new Component();
                        }
                        component.setData(vdata);
                        component.setTag(row + "");
                        component.setText(vdata.getData("text").toString());

                        if (vdata.getData("visible").toString().equals("visible")) {
                            component.setVisible(true);
                            component.setEnable(true);
                            layout.addComponent(component);
                        } else if (vdata.getData("visible").toString().equals("invisible")) {
                            component.setVisible(true);
                            component.setEnable(false);
                            component.setStyle(Style.createStyle().setStyle("opacity", "0"));
                            layout.addComponent(component);
                        } else if (vdata.getData("visible").toString().equals("disable")) {
                            component.setVisible(true);
                            component.setEnable(false);

                            component.setStyle(Style.createStyle().addClass("grayscale").setStyle("opacity", "0.2"));
                            layout.addComponent(component);
                        } else if (vdata.getData("visible").toString().equals("gone")) {
                            component.setVisible(false);
                            component.setEnable(false);
                            //layout.addComponent(component); GONE
                        } else {
                            component.setVisible(vdata.getData("visible").toString().equals("1") ? true : false);
                            component.setEnable(vdata.getData("enable").toString().equals("1") ? true : false);
                            layout.addComponent(component);
                        }

                    } else {
                        Image image = new Image() {
                            public String getClickAction(String action) {
                                if (isEnable() && isVisible()) {
                                    return SmartGrid.this.getFilterAction("actiongrid-" + row + "-" + curraction, row);
                                }
                                return "";
                            }

                            private int row = 0;
                            private String curraction = "";

                            public Image get(int row, String action) {
                                this.row = row;
                                this.curraction = action;
                                return this;
                            }

                        }.get(row, vdata.toString());
                        image.setText(Component.getBaseUrl("/static/img/" + vdata.toString() + ".png"));
                        layout.addComponent(image);
                        component = image;

                    }
                    if (component.isEnable() && component.isVisible()) {
                        component.setStyle(Style.createStyle(component.getStyle()).addClass("nactioncursor"));
                    }
                    if (i >= 1) {
                        component.setStyle(Style.createStyle(component.getStyle()).setStyleIfNoExist("margin-left", "5px"));
                    }
                }
                sb.append(layout.getView(NikitaViewV3.create()));//notag
                sb.append("</td>");
            }
        } else {
            if (row == -1) {
                sb.append("<th style=\"display:none\">");
                sb.append("</th>");
            } else {
                sb.append("<td style=\"display:none\">");
                sb.append("</td>");
            }

        }
        return sb.toString();
    }

    public void setData(Nikitaset data) {
        super.setData(data.toNset());

        maxrows = data.getRows();
        Nset info = new Nset(data.getInfo());
        if (info.getData("nfid").toString().equals("Nset") && info.getData("mode").toString().equals("paging")) {
            maxrows = info.getData("rows").toInteger();
            setCurrentPage(info.getData("page").toInteger());
        }
    }

    public void setData(Nset data) {
        super.setData(data);
        maxrows = data.getSize();
    }

    private SmartGridDetailViewListener smartGridDetailViewListener;

    public interface SmartGridDetailViewListener {

        public NikitaForm onDetailView(SmartGrid parent, int row, Object data);
    }

    public void setSmartGridDetailViewListener(SmartGridDetailViewListener listener) {
        this.smartGridDetailViewListener = listener;
    }

    private SmartGridTextViewListener gridTextViewListener;

    public interface SmartGridTextViewListener {

        public String onTextView(String comp, int row, int col, String text);
    }

    public void setSmartGridTextViewListener(SmartGridTextViewListener listener) {
        this.gridTextViewListener = listener;
    }

    private SmartGridItemViewListener smartGridItemViewListener;

    public interface SmartGridItemViewListener {

        public Component[] onItemView(SmartGrid parent, int row, Object data);
    }

    public void setSmartGridItemViewListener(SmartGridItemViewListener listener) {
        this.smartGridItemViewListener = listener;
    }
    private String curraction = "";

    public void setCurrentAction(String curraction) {
        this.curraction = curraction;
    }

    private String curradetail = "";

    public void setCurrentDetail(String curradetail) {
        this.curradetail = curradetail;
    }

    public String getCurrentDetail() {
        return this.curradetail;
    }

    private int currPage = -1;
    private int rowperPage = -1;
    private int maxrows = 0;
    private int sortcol = -1;

    public int getSortCol() {
        return sortcol;//?
    }

    public int getCurrentPage() {
        return currPage;
    }

    public int getShowPerPage() {
        return rowperPage;
    }

    public void setCurrentPage(int page) {
        currPage = page;
    }
    private Nset currtext = Nset.newArray();

    public void restoreData(Nset data) {
        super.restoreData(data);
        //overide in hire
        //System.out.println(super.getText());

        Nset textdata = Nset.readJSON(super.getText());
        if (textdata.containsKey("sh") || textdata.containsKey("pg")) {
            rowperPage = textdata.getData("sh").toInteger();
            currPage = textdata.getData("pg").toInteger();

            rowperPage = rowperPage <= 0 ? 10 : rowperPage;

            currtext = textdata.getData("t");
            bufferesort = textdata.getData("so");
            bufferedata = Nset.readJSON(textdata.getData("bs").toString());
            if (!bufferedata.isNsetArray()) {
                bufferedata = Nset.newArray();
            }
            Nset n = textdata.getData("su");
            for (int i = 0; i < n.getSize(); i++) {
                if (bufferedata.containsValue(n.getData(i).toString())) {
                    for (int j = 0; j < bufferedata.getSize(); j++) {
                        if (n.getData(i).toString().equals(bufferedata.getData(j).toString())) {
                            bufferedata.removeByIndex(j);
                            break;
                        }
                    }
                }
            }

            n = textdata.getData("st");
            for (int i = 0; i < n.getSize(); i++) {
                if (!bufferedata.containsValue(n.getData(i).toString())) {
                    bufferedata.addData(n.getData(i).toString());
                }
            }

        }
    }
    private String actionSort = "";

    public boolean onActionFilter(String action, String actionNew) {
        if (action.equals("page-view")) {
            return true;
        } else if (action.startsWith("page-sort")) {
            //page-sort-[1]-asc/desc
            actionSort = action;
            return true;
        } else if (action.equals("page-search")) {
            return true;
        } else if (action.startsWith("page-")) {
            if (action.startsWith("page-first")) {
                setCurrentPage(1);
            } else if (action.startsWith("page-back")) {
                int i = getCurrentPage() - 1;
                setCurrentPage(i <= 0 ? 1 : i);
            } else if (action.startsWith("page-next")) {
                setCurrentPage(getCurrentPage() + 1);
            } else if (action.startsWith("page-last")) {
                setCurrentPage(9999999);//max
            } else {
                int i = Utility.getInt(action.substring(5));
                setCurrentPage(i <= 0 ? 1 : i);
            }
            return true;
        }
        return false;
    }

    protected String getViewFilterPage(boolean showrows, NikitaViewV3 v3) {
        StringBuffer sb = new StringBuffer();
        if (getCurrentPage() != -1) {
            Combobox combobox = new Combobox() {
                protected String getClickAction(String action) {
                    return SmartGrid.super.getClickAction("page-view");
                }

                public String getJsId() {
                    return SmartGrid.this.getJsId() + "_show";
                }

                public String getFormJsId() {
                    return SmartGrid.this.getFormJsId();
                }
            };
            combobox.setData(Nset.readsplitString("10|25|50|100|500"));

            combobox.setId(getId() + "_show");
            combobox.setLabel("Show Page : ");
            combobox.setStyle(Style.createStyle().setStyle("width", "60px").setStyle("padding-right", "4px"));
            combobox.setText(getShowPerPage() + "");

            sb.append("<div style=\"float:left;overflow-x: hidden;overflow-y: hidden;\">");
            sb.append(showrows ? combobox.getView(v3) : "");
            sb.append("</div>");

            if (showrows) {
                sb.append("<div style=\"float:left;overflow-x: hidden;overflow-y: hidden; margin-top:10px;\">");
                if (maxrows == 0) {
                    sb.append(" &nbsp;no entries");
                } else {
                    sb.append(" &nbsp;Showing " + Utility.formatCurrency((getCurrentPage() - 1) * getShowPerPage() + 1) + " to " + Utility.formatCurrency(((getCurrentPage() - 1) * getShowPerPage()) + getDataSize()) + " of " + Utility.formatCurrency(maxrows) + " entries");
                }
                sb.append("</div>");
            }

            sb.append("<div style=\"float:right;overflow-x: hidden;overflow-y: hidden;\">");
            sb.append(getPagingString(getCurrentPage(), getShowPerPage(), maxrows));
            sb.append("</div>");
            sb.append("<div style=\"clear:both\"></div>");
        }

        return sb.toString();
    }

    private String getPagingString(int page, int rowperpage, int rows) {
        //System.err.println("getPagingString:"+page+":"+rowperpage+":+"+rows);

        rowperpage = rowperpage <= 0 ? 10 : rowperpage;
        page = page <= 0 ? 1 : page;

        int maxbutton = 6 / 2;
        int pages = (rows / rowperpage) + (rows % rowperpage <= 0 ? 0 : 1);
        StringBuffer sb = new StringBuffer();
        sb.append("<ul class=\"smartpagination\">");
        if (rows > 1) {
            sb.append("<li><a href=\"#\" id=\"").append(SmartGrid.this.getJsId() + "_page").append("\" npage=\"").append(page).append("\"  class=\"smartpaginationfirst ").append(page == 1 ? "disable\"" : "\" " + getFilterAction("page-first", -1)).append("  >&lt;&lt;</a></li>");
            int low = (page - maxbutton) <= 0 ? maxbutton - page : 0;
            int hig = (page + maxbutton) >= pages ? (page + maxbutton) - pages : 0;

            for (int i = Math.max(0, page - maxbutton - hig); i < Math.min(pages, page + maxbutton + low); i++) {

                sb.append("<li><a href=\"#\"  class=\"").append(page == (i + 1) ? "current" : "").append("\"").append(getFilterAction("page-" + (i + 1), -1)).append("  \">" + (i + 1) + "</a></li>");
            }
            sb.append("<li><a href=\"#\"  class=\"smartpaginationlast ").append(page == pages ? "disable\"" : "\" ").append(" \" " + getFilterAction("page-last", -1)).append("  >&gt;&gt;</a></li>");
        }
        sb.append("</ul>");
        return sb.toString();
    }

    protected String getClickAction(String action, int row) {
        if (getOnClickListener() != null && senditemClicked) {
            return getFilterAction(action, row);
        }
        return "";
    }

    private String getFilterAction(String action, int row) {
        return getFilterNewAction(action, "", row);
    }

    private String getFilterNewAction(String action, String newaction, int row) {
        if (row <= 0) {
            row = 0;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" onclick=\"sendactiongrid('").append(getFormJsId()).append("','").append(getJsId()).append("','").append(action).append("','").append(newaction).append("','").append(row).append("')\" ");
        return sb.toString();
    }
}
