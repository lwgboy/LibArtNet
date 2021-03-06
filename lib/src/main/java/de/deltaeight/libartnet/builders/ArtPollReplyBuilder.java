/*
 * LibArtNet
 *
 * Art-Net(TM) Designed by and Copyright Artistic Licence Holdings Ltd
 *
 * Copyright (c) 2020 Julian Rabe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.deltaeight.libartnet.builders;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import de.deltaeight.libartnet.descriptors.ArtNet;
import de.deltaeight.libartnet.descriptors.EquipmentStyle;
import de.deltaeight.libartnet.descriptors.IndicatorState;
import de.deltaeight.libartnet.descriptors.InputStatus;
import de.deltaeight.libartnet.descriptors.OemCode;
import de.deltaeight.libartnet.descriptors.OpCode;
import de.deltaeight.libartnet.descriptors.OutputStatus;
import de.deltaeight.libartnet.descriptors.PortAddressingAuthority;
import de.deltaeight.libartnet.descriptors.PortType;
import de.deltaeight.libartnet.descriptors.Product;
import de.deltaeight.libartnet.packets.ArtPollReply;

/**
 * Builds instances of {@link ArtPollReply}.
 * <p>
 * &nbsp;
 * <table border="1">
 * <caption>Default values</caption>
 * <tr><td>Product</td><td>Unknown product, see {@link OemCode}</td></tr>
 * <tr><td>Indicator state</td><td>{@link IndicatorState#Unknown}</td></tr>
 * <tr><td>Port addressing authority</td><td>{@link PortAddressingAuthority#Unknown}</td></tr>
 * <tr><td>ESTA Manufacturer</td><td>{@code D8}</td></tr>
 * <tr><td>Short name</td><td rowspan="2">{@code LibArtNet}</td></tr>
 * <tr><td>Long name</td></tr>
 * <tr><td>Node report</td><td></td></tr>
 * <tr><td>Port types</td><td>{@link PortType#DEFAULT}</td></tr>
 * <tr><td>Input statuses</td><td>{@link InputStatus#DEFAULT}</td></tr>
 * <tr><td>Output statuses</td><td>{@link OutputStatus#DEFAULT}</td></tr>
 * <tr><td>Equipment style</td><td>{@link EquipmentStyle#Config}</td></tr>
 * </table>
 * <p>
 * See the <a href="https://art-net.org.uk/resources/art-net-specification/">Art-Net Specification</a> for details.
 *
 * @author Julian Rabe
 * @see ArtPollReply
 * @see <a href="https://art-net.org.uk/resources/art-net-specification/">Art-Net Specification</a>
 */
public class ArtPollReplyBuilder extends ArtNetPacketBuilder<ArtPollReply> {

    private static final EquipmentStyle DEFAULT_EQUIPMENT_STYLE = EquipmentStyle.Config;
    private static final byte[] OP_CODE_BYTES = OpCode.OpPollReply.getBytesLittleEndian();

    private final PortType[] portTypes;
    private final InputStatus[] inputStatuses;
    private final OutputStatus[] outputStatuses;
    private final int[] inputUniverseAddresses;
    private final int[] outputUniverseAddresses;
    private final boolean[] macrosActive;
    private final boolean[] remotesActive;
    private byte[] ipAddress;
    private int nodeVersion;
    private int netAddress;
    private int subnetAddress;
    private Product product;
    private int ubeaVersion;
    private IndicatorState indicatorState;
    private PortAddressingAuthority portAddressingAuthority;
    private boolean bootedFromRom;
    private boolean rdmSupport;
    private boolean ubeaPresent;
    private String estaManufacturer;
    private String shortName;
    private String longName;
    private String nodeReport;
    private EquipmentStyle equipmentStyle;
    private byte[] macAddress;
    private byte[] bindIp;
    private int bindIndex;
    private boolean webBrowserConfigurationSupport;
    private boolean ipIsDhcpConfigured;
    private boolean dhcpSupport;
    private boolean longPortAddressSupport;
    private boolean canSwitchToSACN;
    private boolean squawking;

    private boolean changed;
    private ArtPollReply artPollReply;

    public ArtPollReplyBuilder() {

        product = OemCode.getUnknownProduct();
        indicatorState = IndicatorState.Unknown;
        portAddressingAuthority = PortAddressingAuthority.Unknown;
        estaManufacturer = "D8";
        shortName = "LibArtNet";
        longName = "LibArtNet";
        nodeReport = "";
        portTypes = new PortType[]{PortType.DEFAULT, PortType.DEFAULT,
                PortType.DEFAULT, PortType.DEFAULT};
        inputStatuses = new InputStatus[]{InputStatus.DEFAULT,
                InputStatus.DEFAULT, InputStatus.DEFAULT, InputStatus.DEFAULT};
        outputStatuses = new OutputStatus[]{OutputStatus.DEFAULT,
                OutputStatus.DEFAULT, OutputStatus.DEFAULT, OutputStatus.DEFAULT};
        equipmentStyle = DEFAULT_EQUIPMENT_STYLE;
        inputUniverseAddresses = new int[4];
        outputUniverseAddresses = new int[4];
        macrosActive = new boolean[8];
        remotesActive = new boolean[8];
        ipAddress = new byte[4];
        macAddress = new byte[6];
        bindIp = new byte[4];

        changed = true;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link ArtPollReply} instance.
     * @see ArtNetPacketBuilder#build()
     */
    @Override
    public ArtPollReply build() {

        if (changed) {

            byte[] bytes = new byte[239];

            System.arraycopy(ArtNet.HEADER.getBytes(), 0, bytes, 0, 8);
            System.arraycopy(OP_CODE_BYTES, 0, bytes, 8, 2);

            if (ipAddress != null) {
                System.arraycopy(ipAddress, 0, bytes, 10, 4);
            }

            System.arraycopy(ArtNet.PORT.getBytesLittleEndian(), 0, bytes, 14, 2);

            bytes[16] = (byte) (nodeVersion >> 8);
            bytes[17] = (byte) nodeVersion;

            bytes[18] = (byte) netAddress;
            bytes[19] = (byte) subnetAddress;

            bytes[20] = (byte) (product.getProductCode() >> 8);
            bytes[21] = (byte) product.getProductCode();

            bytes[22] = (byte) ubeaVersion;
            bytes[23] |= indicatorState.getValue() << 6;
            bytes[23] |= portAddressingAuthority.getValue() << 4;

            if (bootedFromRom) {
                bytes[23] |= 0b00000100;
            }

            if (rdmSupport) {
                bytes[23] |= 0b00000010;
            }

            if (ubeaPresent) {
                bytes[23] |= 0b00000001;
            }

            if (estaManufacturer.length() > 0) {

                if (estaManufacturer.length() == 2) {
                    bytes[24] = estaManufacturer.getBytes()[1];
                }

                bytes[25] = estaManufacturer.getBytes()[0];
            }

            byte[] shortNameBytes = shortName.getBytes();
            System.arraycopy(shortNameBytes, 0, bytes, 26, shortNameBytes.length);

            byte[] longNameBytes = longName.getBytes();
            System.arraycopy(longNameBytes, 0, bytes, 44, longNameBytes.length);

            byte[] nodeReportBytes = nodeReport.getBytes();
            System.arraycopy(nodeReportBytes, 0, bytes, 108, nodeReportBytes.length);

            int portCounter = 0;
            for (PortType portType : portTypes) {
                if (portType.isOutputSupported() || portType.isInputSupported()) {
                    portCounter++;
                }
            }

            bytes[173] = (byte) portCounter;

            for (int i = 0; i < 4; i++) {
                bytes[174 + i] = portTypes[i].getByte();
                bytes[178 + i] = inputStatuses[i].getByte();
                bytes[182 + i] = outputStatuses[i].getByte();
                bytes[186 + i] = (byte) inputUniverseAddresses[i];
                bytes[190 + i] = (byte) outputUniverseAddresses[i];
            }

            for (int i = 0; i < 8; i++) {
                if (macrosActive[i]) {
                    bytes[195] |= 0b00000001 << i;
                }
                if (remotesActive[i]) {
                    bytes[196] |= 0b00000001 << i;
                }
            }

            bytes[200] = equipmentStyle.getValue();

            System.arraycopy(macAddress, 0, bytes, 201, 6);
            System.arraycopy(bindIp, 0, bytes, 207, 4);

            bytes[211] = (byte) bindIndex;

            if (webBrowserConfigurationSupport) {
                bytes[212] |= 0b00000001;
            }

            if (ipIsDhcpConfigured) {
                bytes[212] |= 0b00000010;
            }

            if (dhcpSupport) {
                bytes[212] |= 0b00000100;
            }

            if (longPortAddressSupport) {
                bytes[212] |= 0b00001000;
            }

            if (canSwitchToSACN) {
                bytes[212] |= 0b00010000;
            }

            if (squawking) {
                bytes[212] |= 0b00100000;
            }

            artPollReply = new ArtPollReply(getIpAddress(), getNodeVersion(), getNetAddress(), getSubnetAddress(),
                    getProduct(), getUbeaVersion(), getIndicatorState(), getPortAddressingAuthority(), isBootedFromRom(),
                    supportsRdm(), isUbeaPresent(), getEstaManufacturer(), getShortName(), getLongName(),
                    getNodeReport(), getPortTypes(), getInputStatuses(), getOutputStatuses(),
                    getInputUniverseAddresses(), getOutputUniverseAddresses(), getMacrosActive(), getRemotesActive(),
                    getEquipmentStyle(), getMacAddress(), getBindIp(), getBindIndex(), supportsWebBrowserConfiguration(),
                    ipIsDhcpConfigured(), supportsDhcp(), supportsLongPortAddresses(), canSwitchToSACN(), isSquawking(),
                    bytes.clone());

            changed = false;
        }

        return artPollReply;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link ArtPollReply} instance.
     * @see ArtNetPacketBuilder#build()
     */
    @Override
    public ArtPollReply buildFromBytes(byte[] packetData) {
        if (packetData[8] == OP_CODE_BYTES[0] && packetData[9] == OP_CODE_BYTES[1]) {

            return new ArtPollReply(
                    Arrays.copyOfRange(packetData, 10, 14),
                    packetData[16] << 8 | packetData[17] & 0xFF,
                    packetData[18],
                    packetData[19],
                    OemCode.getProductByProductCode(packetData[20] << 8 | packetData[21] & 0xFF),
                    packetData[22] & 0xFF,
                    IndicatorState.values()[packetData[23] >> 6 & 0b00000011],
                    PortAddressingAuthority.values()[packetData[23] >> 4 & 0b00000011],
                    (packetData[23] & 0b00000100) > 0,
                    (packetData[23] & 0b00000010) > 0,
                    (packetData[23] & 0b00000001) > 0,
                    computeStringWithNullTermination(new byte[]{packetData[25], packetData[24]}),
                    computeStringWithNullTermination(Arrays.copyOfRange(packetData, 26, 43)),
                    computeStringWithNullTermination(Arrays.copyOfRange(packetData, 44, 107)),
                    computeStringWithNullTermination(Arrays.copyOfRange(packetData, 108, 173)),
                    new PortType[]{
                            PortType.buildFromByte(packetData[174]),
                            PortType.buildFromByte(packetData[175]),
                            PortType.buildFromByte(packetData[176]),
                            PortType.buildFromByte(packetData[177])
                    },
                    new InputStatus[]{
                            InputStatus.buildFromByte(packetData[178]),
                            InputStatus.buildFromByte(packetData[179]),
                            InputStatus.buildFromByte(packetData[180]),
                            InputStatus.buildFromByte(packetData[181])
                    },
                    new OutputStatus[]{
                            OutputStatus.buildFromByte(packetData[182]),
                            OutputStatus.buildFromByte(packetData[183]),
                            OutputStatus.buildFromByte(packetData[184]),
                            OutputStatus.buildFromByte(packetData[185])
                    },
                    new int[]{packetData[186], packetData[187], packetData[188], packetData[189]},
                    new int[]{packetData[190], packetData[191], packetData[192], packetData[193]},
                    new boolean[]{
                            (packetData[195] & 0b00000001) > 0,
                            (packetData[195] & 0b00000010) > 0,
                            (packetData[195] & 0b00000100) > 0,
                            (packetData[195] & 0b00001000) > 0,
                            (packetData[195] & 0b00010000) > 0,
                            (packetData[195] & 0b00100000) > 0,
                            (packetData[195] & 0b01000000) > 0,
                            (packetData[195] & 0b10000000) > 0
                    },
                    new boolean[]{
                            (packetData[196] & 0b00000001) > 0,
                            (packetData[196] & 0b00000010) > 0,
                            (packetData[196] & 0b00000100) > 0,
                            (packetData[196] & 0b00001000) > 0,
                            (packetData[196] & 0b00010000) > 0,
                            (packetData[196] & 0b00100000) > 0,
                            (packetData[196] & 0b01000000) > 0,
                            (packetData[196] & 0b10000000) > 0
                    },
                    EquipmentStyle.getEquipmentStyle(packetData[200]),
                    Arrays.copyOfRange(packetData, 201, 207),
                    Arrays.copyOfRange(packetData, 207, 211),
                    packetData[211] & 0xFF,
                    (packetData[212] & 0b00000001) > 0,
                    (packetData[212] & 0b00000010) > 0,
                    (packetData[212] & 0b00000100) > 0,
                    (packetData[212] & 0b00001000) > 0,
                    (packetData[212] & 0b00010000) > 0,
                    (packetData[212] & 0b00100000) > 0,
                    packetData);
        }
        return null;
    }

    private String computeStringWithNullTermination(byte[] input) {

        int nullIndex = -1;
        for (int i = 0; i < input.length; i++) {
            if(input[i] == 0x00) {
                nullIndex = i;
                break;
            }
        }

        if(nullIndex > -1) {
            return new String(Arrays.copyOfRange(input, 0, nullIndex), StandardCharsets.US_ASCII);
        }

        return new String(input, StandardCharsets.US_ASCII);
    }

    public byte[] getIpAddress() {
        return ipAddress.clone();
    }

    public void setIpAddress(byte[] ipAddress) {

        if (ipAddress == null) {
            ipAddress = new byte[4];
        }

        if (ipAddress.length != 4) {
            throw new IllegalArgumentException("Illegal IP Address!");
        }

        if (!Arrays.equals(this.ipAddress, ipAddress)) {
            this.ipAddress = ipAddress.clone();
            changed = true;
        }
    }

    public ArtPollReplyBuilder withIpAddress(byte[] ipAddress) {
        setIpAddress(ipAddress);
        return this;
    }

    public int getNodeVersion() {
        return nodeVersion;
    }

    public void setNodeVersion(int nodeVersion) {
        if (this.nodeVersion != nodeVersion) {
            this.nodeVersion = nodeVersion;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withNodeVersion(int nodeVersion) {
        setNodeVersion(nodeVersion);
        return this;
    }

    public int getNetAddress() {
        return netAddress;
    }

    public void setNetAddress(int netAddress) {
        if (this.netAddress != netAddress) {
            if (0 > netAddress || netAddress > 127) {
                throw new IllegalArgumentException("Illegal net address!");
            }
            this.netAddress = netAddress;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withNetAddress(int net) {
        setNetAddress(net);
        return this;
    }

    public int getSubnetAddress() {
        return subnetAddress;
    }

    public void setSubnetAddress(int subnetAddress) {
        if (this.subnetAddress != subnetAddress) {
            if (0 > subnetAddress || subnetAddress > 15) {
                throw new IllegalArgumentException("Illegal subnet address!");
            }
            this.subnetAddress = subnetAddress;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withSubnetAddress(int subnet) {
        setSubnetAddress(subnet);
        return this;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {

        if (product == null) {
            product = OemCode.getUnknownProduct();
        }

        if (this.product != product) {
            this.product = product;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withProduct(Product product) {
        setProduct(product);
        return this;
    }

    public int getUbeaVersion() {
        return ubeaVersion;
    }

    public void setUbeaVersion(int ubeaVersion) {
        if (this.ubeaVersion != ubeaVersion) {
            if (0 > ubeaVersion || ubeaVersion > 255) {
                throw new IllegalArgumentException("Illegal UBEA version!");
            }
            this.ubeaVersion = ubeaVersion;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withUbeaVersion(int ubeaVersion) {
        setUbeaVersion(ubeaVersion);
        return this;
    }

    public IndicatorState getIndicatorState() {
        return indicatorState;
    }

    public void setIndicatorState(IndicatorState indicatorState) {

        if (indicatorState == null) {
            indicatorState = IndicatorState.Unknown;
        }

        if (this.indicatorState != indicatorState) {
            this.indicatorState = indicatorState;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withIndicatorState(IndicatorState indicatorState) {
        setIndicatorState(indicatorState);
        return this;
    }

    public PortAddressingAuthority getPortAddressingAuthority() {
        return portAddressingAuthority;
    }

    public void setPortAddressingAuthority(PortAddressingAuthority portAddressingAuthority) {

        if (portAddressingAuthority == null) {
            portAddressingAuthority = PortAddressingAuthority.Unknown;
        }

        if (this.portAddressingAuthority != portAddressingAuthority) {
            this.portAddressingAuthority = portAddressingAuthority;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withPortAddressingAuthority(PortAddressingAuthority portAddressingAuthority) {
        setPortAddressingAuthority(portAddressingAuthority);
        return this;
    }

    public boolean isBootedFromRom() {
        return bootedFromRom;
    }

    public void setBootedFromRom(boolean bootedFromRom) {
        if (this.bootedFromRom != bootedFromRom) {
            this.bootedFromRom = bootedFromRom;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withBootedFromRom(boolean bootedFromRom) {
        setBootedFromRom(bootedFromRom);
        return this;
    }

    public boolean supportsRdm() {
        return rdmSupport;
    }

    public void setRdmSupport(boolean rdmSupport) {
        if (this.rdmSupport != rdmSupport) {
            this.rdmSupport = rdmSupport;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withRdmSupport(boolean rdmSupported) {
        setRdmSupport(rdmSupported);
        return this;
    }

    public boolean isUbeaPresent() {
        return ubeaPresent;
    }

    public void setUbeaPresent(boolean ubeaPresent) {
        if (this.ubeaPresent != ubeaPresent) {
            this.ubeaPresent = ubeaPresent;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withUbeaPresent(boolean ubeaPresent) {
        setUbeaPresent(ubeaPresent);
        return this;
    }

    public String getEstaManufacturer() {
        return estaManufacturer;
    }

    public void setEstaManufacturer(String estaManufacturer) {
        if (!this.estaManufacturer.equals(estaManufacturer)) {
            if (estaManufacturer == null) {
                estaManufacturer = "";
            }
            this.estaManufacturer = estaManufacturer.substring(0, Math.min(estaManufacturer.length(), 2));
            changed = true;
        }
    }

    public ArtPollReplyBuilder withEstaManufacturer(String estaManufacturer) {
        setEstaManufacturer(estaManufacturer);
        return this;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        if (!this.shortName.equals(shortName)) {
            if (shortName == null) {
                shortName = "";
            }
            this.shortName = shortName.substring(0, Math.min(shortName.length(), 17));
            changed = true;
        }
    }

    public ArtPollReplyBuilder withShortName(String shortName) {
        setShortName(shortName);
        return this;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        if (!this.longName.equals(longName)) {
            if (longName == null) {
                longName = "";
            }
            this.longName = longName.substring(0, Math.min(longName.length(), 63));
            changed = true;
        }
    }

    public ArtPollReplyBuilder withLongName(String longName) {
        setLongName(longName);
        return this;
    }

    public String getNodeReport() {
        return nodeReport;
    }

    public void setNodeReport(String nodeReport) {
        if (!this.nodeReport.equals(nodeReport)) {
            if (nodeReport == null) {
                nodeReport = "";
            }
            this.nodeReport = nodeReport.substring(0, Math.min(nodeReport.length(), 63));
            changed = true;
        }
    }

    public ArtPollReplyBuilder withNodeReport(String nodeReport) {
        setNodeReport(nodeReport);
        return this;
    }

    public PortType[] getPortTypes() {
        return portTypes.clone();
    }

    public PortType getPortType(int index) {
        return portTypes[index];
    }

    public void setPortType(int index, PortType portType) {

        if (portType == null) {
            portType = PortType.DEFAULT;
        }

        if (!this.portTypes[index].equals(portType)) {
            portTypes[index] = portType;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withPortType(int index, PortType portType) {
        setPortType(index, portType);
        return this;
    }

    public InputStatus[] getInputStatuses() {
        return inputStatuses.clone();
    }

    public InputStatus getInputStatus(int index) {
        return inputStatuses[index];
    }

    public void setInputStatus(int index, InputStatus inputStatus) {

        if (inputStatus == null) {
            inputStatus = InputStatus.DEFAULT;
        }

        if (!this.inputStatuses[index].equals(inputStatus)) {
            inputStatuses[index] = inputStatus;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withInputStatus(int index, InputStatus inputStatus) {
        setInputStatus(index, inputStatus);
        return this;
    }

    public OutputStatus[] getOutputStatuses() {
        return outputStatuses.clone();
    }

    public OutputStatus getOutputStatus(int index) {
        return outputStatuses[index];
    }

    public void setOutputStatus(int index, OutputStatus outputStatus) {

        if (outputStatus == null) {
            outputStatus = OutputStatus.DEFAULT;
        }

        if (!this.outputStatuses[index].equals(outputStatus)) {
            outputStatuses[index] = outputStatus;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withOutputStatus(int index, OutputStatus outputStatus) {
        setOutputStatus(index, outputStatus);
        return this;
    }

    public int[] getInputUniverseAddresses() {
        return inputUniverseAddresses.clone();
    }

    public int getInputUniverseAddress(int index) {
        return inputUniverseAddresses[index];
    }

    public void setInputUniverseAddress(int index, int inputUniverseAddress) {
        if (inputUniverseAddresses[index] != inputUniverseAddress) {
            if (0 > inputUniverseAddress || inputUniverseAddress > 15) {
                throw new IllegalArgumentException("Illegal universe address!");
            }
            inputUniverseAddresses[index] = inputUniverseAddress;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withInputUniverseAddress(int index, int inputUniverseAddress) {
        setInputUniverseAddress(index, inputUniverseAddress);
        return this;
    }

    public int[] getOutputUniverseAddresses() {
        return outputUniverseAddresses.clone();
    }

    public int getOutputUniverseAddress(int index) {
        return outputUniverseAddresses[index];
    }

    public void setOutputUniverseAddress(int index, int outputUniverseAddress) {
        if (outputUniverseAddresses[index] != outputUniverseAddress) {
            if (0 > outputUniverseAddress || outputUniverseAddress > 15) {
                throw new IllegalArgumentException("Illegal universe address!");
            }
            outputUniverseAddresses[index] = outputUniverseAddress;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withOutputUniverseAddress(int index, int outputUniverseAddress) {
        setOutputUniverseAddress(index, outputUniverseAddress);
        return this;
    }

    public boolean[] getMacrosActive() {
        return macrosActive.clone();
    }

    public boolean isMacroActive(int index) {
        return macrosActive[index];
    }

    public void setMacroActive(int index, boolean isActive) {
        if (macrosActive[index] != isActive) {
            macrosActive[index] = isActive;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withMacroActive(int index, boolean isActive) {
        setMacroActive(index, isActive);
        return this;
    }

    public boolean[] getRemotesActive() {
        return remotesActive.clone();
    }

    public boolean isRemoteActive(int index) {
        return remotesActive[index];
    }

    public void setRemoteActive(int index, boolean isActive) {
        if (remotesActive[index] != isActive) {
            remotesActive[index] = isActive;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withRemoteActive(int index, boolean isActive) {
        setRemoteActive(index, isActive);
        return this;
    }

    public EquipmentStyle getEquipmentStyle() {
        return equipmentStyle;
    }

    public void setEquipmentStyle(EquipmentStyle equipmentStyle) {

        if (equipmentStyle == null) {
            equipmentStyle = DEFAULT_EQUIPMENT_STYLE;
        }

        if (this.equipmentStyle != equipmentStyle) {
            this.equipmentStyle = equipmentStyle;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withEquipmentStyle(EquipmentStyle equipmentStyle) {
        setEquipmentStyle(equipmentStyle);
        return this;
    }

    public byte[] getMacAddress() {
        return macAddress.clone();
    }

    public void setMacAddress(byte[] macAddress) {

        if (macAddress == null) {
            macAddress = new byte[6];
        }

        if (macAddress.length != 6) {
            throw new IllegalArgumentException("Illegal MAC address!");
        }

        if (!Arrays.equals(this.macAddress, macAddress)) {
            this.macAddress = macAddress.clone();
            changed = true;
        }
    }

    public ArtPollReplyBuilder withMacAddress(byte[] macAddress) {
        setMacAddress(macAddress);
        return this;
    }

    public byte[] getBindIp() {
        return bindIp.clone();
    }

    public void setBindIp(byte[] bindIp) {

        if (bindIp == null) {
            bindIp = new byte[4];
        }

        if (bindIp.length != 4) {
            throw new IllegalArgumentException("Illegal bind IP address!");
        }

        if (!Arrays.equals(this.bindIp, bindIp)) {
            this.bindIp = bindIp.clone();
            changed = true;
        }
    }

    public ArtPollReplyBuilder withBindIp(byte[] bindIp) {
        setBindIp(bindIp);
        return this;
    }

    public int getBindIndex() {
        return bindIndex;
    }

    public void setBindIndex(int bindIndex) {
        if (this.bindIndex != bindIndex) {
            if (0 > bindIndex || bindIndex > 255) {
                throw new IllegalArgumentException("Illegal bind index!");
            }
            this.bindIndex = bindIndex;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withBindIndex(int bindIndex) {
        setBindIndex(bindIndex);
        return this;
    }

    public boolean supportsWebBrowserConfiguration() {
        return webBrowserConfigurationSupport;
    }

    public void setWebBrowserConfigurationSupport(boolean webBrowserConfigurationSupport) {
        if (this.webBrowserConfigurationSupport != webBrowserConfigurationSupport) {
            this.webBrowserConfigurationSupport = webBrowserConfigurationSupport;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withWebBrowserConfigurationSupport(boolean webBrowserConfigurationSupport) {
        setWebBrowserConfigurationSupport(webBrowserConfigurationSupport);
        return this;
    }

    public boolean ipIsDhcpConfigured() {
        return ipIsDhcpConfigured;
    }

    public void setIpIsDhcpConfigured(boolean ipIsDhcpConfigured) {
        if (this.ipIsDhcpConfigured != ipIsDhcpConfigured) {
            this.ipIsDhcpConfigured = ipIsDhcpConfigured;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withIpIsDhcpConfigured(boolean ipIsDhcpConfigured) {
        setIpIsDhcpConfigured(ipIsDhcpConfigured);
        return this;
    }

    public boolean supportsDhcp() {
        return dhcpSupport;
    }

    public void setDhcpSupport(boolean dhcpSupport) {
        if (this.dhcpSupport != dhcpSupport) {
            this.dhcpSupport = dhcpSupport;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withDhcpSupport(boolean dhcpSupport) {
        setDhcpSupport(dhcpSupport);
        return this;
    }

    public boolean supportsLongPortAddresses() {
        return longPortAddressSupport;
    }

    public void setLongPortAddressSupport(boolean longPortAddressSupport) {
        if (this.longPortAddressSupport != longPortAddressSupport) {
            this.longPortAddressSupport = longPortAddressSupport;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withLongPortAddressSupport(boolean longPortAddressSupport) {
        setLongPortAddressSupport(longPortAddressSupport);
        return this;
    }

    public boolean canSwitchToSACN() {
        return canSwitchToSACN;
    }

    public void setCanSwitchToSACN(boolean canSwitchToSACN) {
        if (this.canSwitchToSACN != canSwitchToSACN) {
            this.canSwitchToSACN = canSwitchToSACN;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withCanSwitchToSACN(boolean canSwitchToSACN) {
        setCanSwitchToSACN(canSwitchToSACN);
        return this;
    }

    public boolean isSquawking() {
        return squawking;
    }

    public void setSquawking(boolean squawking) {
        if (this.squawking != squawking) {
            this.squawking = squawking;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withSquawking(boolean squawking) {
        setSquawking(squawking);
        return this;
    }
}
