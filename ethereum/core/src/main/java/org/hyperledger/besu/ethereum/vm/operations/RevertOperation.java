/*
 * Copyright ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.hyperledger.besu.ethereum.vm.operations;

import org.hyperledger.besu.ethereum.core.Gas;
import org.hyperledger.besu.ethereum.vm.AbstractOperation;
import org.hyperledger.besu.ethereum.vm.GasCalculator;
import org.hyperledger.besu.ethereum.vm.MessageFrame;

import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.units.bigints.UInt256;

public class RevertOperation extends AbstractOperation {

  public RevertOperation(final GasCalculator gasCalculator) {
    super(0xFD, "REVERT", 2, 0, false, 1, gasCalculator);
  }

  @Override
  public Gas cost(final MessageFrame frame) {
    final UInt256 offset = UInt256.fromBytes(frame.getStackItem(0));
    final UInt256 length = UInt256.fromBytes(frame.getStackItem(1));

    return gasCalculator().memoryExpansionGasCost(frame, offset, length);
  }

  @Override
  public void execute(final MessageFrame frame) {
    final UInt256 from = UInt256.fromBytes(frame.popStackItem());
    final UInt256 length = UInt256.fromBytes(frame.popStackItem());
    Bytes reason = frame.readMemory(from, length);
    frame.setOutputData(reason);
    frame.setRevertReason(reason);
    frame.setState(MessageFrame.State.REVERT);
  }
}
