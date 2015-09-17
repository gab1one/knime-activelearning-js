/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2015
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * --------------------------------------------------------------------- *
 *
 */

knime_al_loopend = function() {

    view = {};

    view.name = "knime_al_loopend";

    view.init = function(representation, value) {

        // check if data is avaiable
        if ((!representation.rowIDs) || representation.rowIDs.length < 1) {
            d3.select("body").text("Error: No data available");
            return;
        }
        // the result object
        var _value = value;

        // for the multiple rows implementation
        var _multiRow = representation.rowIDs.length > 1;
        var _curRowIdx = 0;
        var _rows = representation.rowIDs;

        // for create Img
        var _format = representation.format;
        var _port = representation.serverPort;
        var _host = representation.hostAddress;

        // will be initialized by chachDom function
        var $imgDiv = null;
        var $labelSelect = null;
        var $labelForm = null;

        // creates the DOM
        this.createDOM = function() {
            var body = document.getElementsByTagName("body")[0];

            // ImgView
            var imgDiv = document.createElement("div");
            imgDiv.class = "quickformcontainer";
            imgDiv.id = "imgDiv";
            body.appendChild(imgDiv);

            // Labelselection container
            var label_div = document.createElement("div");
            label_div.id = "label_div";
            label_div.class = "quickformcontainer";
            body.appendChild(label_div);

            // class labels select
            var _label_select = document.createElement("select");
            _label_select.name = "label select";
            _label_select.id = "label_select";
            label_div.appendChild(_label_select);

            var label_form = document.createElement("form");
            label_form.id = "label_form";
            label_div.appendChild(label_form);

            // class label input
            var label_input = document.createElement("input");
            label_input.id = "label_input";
            label_input.type = "text";
            label_input.name = "Class Label";
            label_form.appendChild(label_input);

            // add class button
            var add_btn = document.createElement("input");
            add_btn.type = "submit";
            add_btn.value = "Add Class Label";
            label_form.appendChild(add_btn);

            // add forward and backwards buttons
            if (_multiRow) {
                var control = document.createElement("div");
                var fwd_btn = document.createElement("button");
                fwd_btn.value = "Next Row";
                fwd_btn.innerHTML = "Next Row";
                fwd_btn.id = "fwd_btn";

                var back_btn = document.createElement("button");
                back_btn.value = "Previous Row";
                back_btn.innerHTML = "Previous Row";
                back_btn.id = "back_btn";

                control.appendChild(back_btn);
                control.appendChild(fwd_btn);
                body.appendChild(control);
            }
        };

        // FUNCTIONS

        this.cacheDOM = function() {
            $imgDiv = $("imgDiv");
            $labelForm = $("#label_form");
            $labelInput = $("#label_input");
            $labelSelect = $("#label_select");

            // Add all previously defined class labels as <option>s
            for (var j = 0; j < _value.classLabels.length; j++) {
                this.addLabel(null, _value.classLabels[j]);
            }
        };

        // bind the event listeners
        this.bindListeners = function() {
            // on click funktions
            label_form.onsubmit = this.addLabel.bind(this);

            if (_multiRow) {
                fwd_btn.onclick = this.nextRow.bind(this);
                back_btn.onclick = this.prevRow.bind(this);
            }
        };

        // getter for the value
        this.getValue = function() {
            return _value;
        };

        // sets the label of the current row to the currently selected value in
        // the label select
        this.setSelectedLabel = function() {
            _value.rowLabels[_rows[_curRowIdx]] = $labelSelect.val();
        };

        /**
         * @param checkLabel
         *            the label to check
         * @returns true if the given label is known to the model
         */
        this.labelExists = function(checkLabel) {
            return $labelSelect.find("#label-" + checkLabel).length;
        };

        // refreshes the view
        this.refreshView = function() {
            var selected_row = _rows[_curRowIdx];

            // refresh the representation view
            $("#repElement").remove();
            imgDiv.appendChild(this.createRepView(selected_row, _format));

            // refresh the label
            var currentLabel = _value.rowLabels[selected_row];
            var exists = this.labelExists(currentLabel);
            if (exists) {
                $labelSelect.val(currentLabel);
            }
        };

        // add a new label and select it
        this.addLabel = function(event, label) {
            var _label = null;

            // called from the label onSubmit.
            if (event) {
                event.preventDefault();
                _label = $labelInput.val();
                $labelInput.val(""); // clear input box
            } else { // called from constructor
                _label = label;
            }
            if (_label.length === 0) {// don't add empty labels
                return;
            }

            // don't add twice
            var exists = this.labelExists(_label);
            if (!exists) {
                var label_opt = document.createElement("option");
                label_opt.value = _label;
                label_opt.innerHTML = _label;
                label_opt.class = "label_opt";
                label_opt.id = "label-" + _label;
                $labelSelect.append(label_opt);
            }
            $labelSelect.val(_label);
        };

        // function to show the previous row
        this.prevRow = function() {
            this.setSelectedLabel();

            // wrap around
            if (_curRowIdx === 0) {
                _curRowIdx = _rows.length - 1;
            } else {
                _curRowIdx = _curRowIdx - 1;
            }
            this.refreshView();
        };

        // function to show the next row
        this.nextRow = function() {
            this.setSelectedLabel();

            // wrap around
            if (_curRowIdx === _rows.length - 1) {
                _curRowIdx = 0;
            } else {
                _curRowIdx = _curRowIdx + 1;
            }
            this.refreshView();
        };

        // function to create representation div
        this.createRepView = function(rowID, format) {

            if (format == "PNG") {
                var img = document.createElement("img");
                img.setAttribute("src", _host + ":" + _port + "/" + rowID);
                img.setAttribute("id", "repElement");

                img.style.maxWidth = 300 + "px";
                img.style.maxHeight = 400 + "px";

                return img;
            } else {
                var errorText = "Input format not supported: " + format;
                return document.createTextNode(errorText);
            }
        };

        this.createDOM();
        this.cacheDOM();
        this.bindListeners();
        this.refreshView();

        resizeParent();
    };

    view.validate = function() {

        // ensure the last label is set.
        view.setSelectedLabel();
        return true;
    };

    view.setValidationErrorMessage = function(message) {
    };

    view.getComponentValue = function() {
        return view.getValue();
    };

    return view;

}();
