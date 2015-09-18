/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by
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
 * ---------------------------------------------------------------------
 *
 * History
 *   6 Mar 2015 (gabriel): created
 */
package org.knime.al.js.nodes.webportal.annotator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.knime.al.js.nodes.webportal.ActiveLearnWebPortalNode;
import org.knime.al.js.util.ActiveLearnJSCommon.RepesentationType;
import org.knime.al.js.util.server.ActiveLearnJSFileServer;
import org.knime.al.nodes.loop.ActiveLearnLoopUtils;
import org.knime.al.util.NodeUtils;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.DataValue;
import org.knime.core.data.RowKey;
import org.knime.core.data.StringValue;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortType;
import org.knime.core.node.web.ValidationError;
import org.knime.js.core.node.AbstractWizardNodeModel;
import org.knime.knip.base.data.img.ImgPlusValue;

/**
 *
 * @author <a href="mailto:gabriel.einsdorf@uni.kn">Gabriel Einsdorf</a>
 */
public class ActiveLearnAnnotatorNodeModel extends
        AbstractWizardNodeModel<ActiveLearnAnnotatorViewRepresentation, ActiveLearnAnnotatorViewValue>
        implements ActiveLearnWebPortalNode {

    private static final NodeLogger LOGGER = NodeLogger
            .getLogger(ActiveLearnAnnotatorNodeModel.class);

    private final SettingsModelString m_repColModel = ActiveLearnAnnotatorSettingsModels
            .createRepColumnModel();
    private final SettingsModelInteger m_serverPortModel = ActiveLearnAnnotatorSettingsModels
            .createServerPortModel();
    private final SettingsModelString m_hostAdress = ActiveLearnAnnotatorSettingsModels
            .createHostAddressModel();

    private ActiveLearnJSFileServer m_fileServer;

    private static final int LEARNING_DATA = 0;
    private static final int OUTPORT = 1;

    private int m_repColIdx;
    private int m_previousIteration = -1;

    private List<SettingsModel> m_settingsModels;
    private Map<String, DataCell> m_repMap;
    private String m_format;

    /**
    *
    */
    public ActiveLearnAnnotatorNodeModel() {

        super(new PortType[] { BufferedDataTable.TYPE },
                new PortType[] { BufferedDataTable.TYPE }, "Al View");
        collectSettingsModels();
    }

    /**
     * {@inheritDoc} Node routes through the second input port.
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {

        m_repColIdx = NodeUtils.autoColumnSelection(inSpecs[LEARNING_DATA],
                m_repColModel, DataValue.class, this.getClass());

        final DataTableSpec spec = new DataTableSpec(
                new DataColumnSpecCreator("Labels", StringCell.TYPE)
                        .createSpec());
        return new DataTableSpec[] { spec };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecute(final PortObject[] inObjects,
            final ExecutionContext exec) throws Exception {

        final int currentIteration = getAvailableFlowVariables()
                .get(ActiveLearnLoopUtils.AL_STEP).getIntValue();

        final DataTableSpec[] outSpec = configure(new DataTableSpec[] {
                (DataTableSpec) inObjects[LEARNING_DATA].getSpec() });

        final BufferedDataContainer outcontainer = exec
                .createDataContainer(outSpec[0]);

        // user pressed the apply button
        if (currentIteration == m_previousIteration) {
            synchronized (getLock()) {

                final int continueExecution = getViewValue()
                        .getContinueExecution() ? 1 : 0;
                pushFlowVariableInt(ALControlVar, continueExecution);

                // Add the class labels
                getViewValue().getRowLabels()
                        .forEach((final String key, final String value) -> {
                            outcontainer.addRowToTable(new DefaultRow(
                                    new RowKey(key), new StringCell(value)));
                        });
            }
        } else {
            // normal execution
            m_previousIteration = currentIteration;

            final BufferedDataTable learningData = (BufferedDataTable) inObjects[LEARNING_DATA];

            m_repColIdx = learningData.getDataTableSpec()
                    .findColumnIndex(m_repColModel.getStringValue());

            m_format = setFormat(learningData.getDataTableSpec()
                    .getColumnSpec(m_repColIdx).getType());

            // updated learning count
            synchronized (getLock()) {

                final List<String> rowIDs = new ArrayList<>(
                        learningData.getRowCount());

                m_repMap = new HashMap<>(learningData.getRowCount());

                final HashMap<String, String> viewmap = new HashMap<>();

                learningData.forEach((row) -> {
                    rowIDs.add(row.getKey().getString());
                    m_repMap.put(row.getKey().getString(),
                            row.getCell(m_repColIdx));
                    viewmap.put(row.getKey().getString(), "");
                });

                final ActiveLearnAnnotatorViewRepresentation rep = new ActiveLearnAnnotatorViewRepresentation(
                        rowIDs, m_hostAdress.getStringValue(),
                        m_serverPortModel.getIntValue(), m_format);
                setViewRepresentation(rep);

                final ActiveLearnAnnotatorViewValue viewVal = new ActiveLearnAnnotatorViewValue(
                        Collections.EMPTY_SET, viewmap, true);
                setViewValue(viewVal);

                m_fileServer = new ActiveLearnJSFileServer(
                        m_serverPortModel.getIntValue(), m_repMap,
                        learningData.getSpec().getColumnSpec(m_repColIdx));
            }
        }

        outcontainer.close();
        return new BufferedDataTable[] { outcontainer.getTable() };
    }

    /**
     * @param type
     * @return
     * @throws InvalidSettingsException
     */
    private String setFormat(final DataType type)
            throws InvalidSettingsException {
        if (type.isCompatible(ImgPlusValue.class)) {
            return RepesentationType.PNG.toString();
        } else if (type.isCompatible(StringValue.class)) {
            return RepesentationType.TXT.toString();
        } else {
            throw new InvalidSettingsException(
                    "The selected representative collumn is not supported,");
        }
    }

    /**
     * Initializes the settings model storage.
     *
     * @return a list of all settings models used in the node
     */
    protected List<SettingsModel> collectSettingsModels() {
        if (m_settingsModels == null) {
            m_settingsModels = new ArrayList<SettingsModel>(2);
            m_settingsModels.add(m_repColModel);
            m_settingsModels.add(m_serverPortModel);
        }
        return m_settingsModels;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCurrentValue(final NodeSettingsWO content) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActiveLearnAnnotatorViewRepresentation createEmptyViewRepresentation() {
        return new ActiveLearnAnnotatorViewRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.activelearning.js.loopend";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHideInWizard() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performReset() {
        synchronized (getLock()) {
            m_previousIteration = -1; // To ensure node gets executed again
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        LOGGER.warn("This option is not enabled!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        collectSettingsModels();
        m_settingsModels.forEach((model) -> model.saveSettingsTo(settings));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        collectSettingsModels();
        for (final SettingsModel model : m_settingsModels) {
            model.validateSettings(settings);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        collectSettingsModels();
        for (final SettingsModel model : m_settingsModels) {
            model.loadSettingsFrom(settings);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActiveLearnAnnotatorViewValue createEmptyViewValue() {
        return new ActiveLearnAnnotatorViewValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(
            final ActiveLearnAnnotatorViewValue viewContent) {
        return null;
    }
}
