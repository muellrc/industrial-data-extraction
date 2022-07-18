#!/usr/bin/env python
# -*- coding: utf_8 -*-
"""
 Modbus Client simulator based on the Modbus TestKit Library (modbus_tk)
"""

from __future__ import print_function

import modbus_tk
import modbus_tk.defines as cst
from modbus_tk import modbus_tcp, hooks
import logging

def main():
    """main"""
    logger = modbus_tk.utils.create_logger("console", level=logging.DEBUG)

    def on_after_recv(data):
        master, bytes_data = data
        logger.info(bytes_data)

    hooks.install_hook('modbus.Master.after_recv', on_after_recv)
    try:

        def on_before_connect(args):
            master = args[0]
            logger.debug("on_before_connect {0} {1}".format(master._host, master._port))

        hooks.install_hook("modbus_tcp.TcpMaster.before_connect", on_before_connect)

        def on_after_recv(args):
            response = args[1]
            logger.debug("on_after_recv {0} bytes received".format(len(response)))

        hooks.install_hook("modbus_tcp.TcpMaster.after_recv", on_after_recv)

        # Connect to the Server
        master = modbus_tcp.TcpMaster()
        master.set_timeout(5.0)
        logger.info("connected")

        # Writes to 400002 and 400003
        logger.info(master.execute(1, cst.WRITE_SINGLE_REGISTER, 1, output_value=100))
        logger.info(master.execute(1, cst.WRITE_SINGLE_REGISTER, 2, output_value=200))
        #Outputs values in 400002 and 400003
        logger.info(master.execute(1, cst.READ_HOLDING_REGISTERS, 1, 2))


    except modbus_tk.modbus.ModbusError as exc:
        logger.error("%s- Code=%d", exc, exc.get_exception_code())

if __name__ == "__main__":
    main()
