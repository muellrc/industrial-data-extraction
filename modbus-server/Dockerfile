FROM prefecthq/prefect:latest-python3.9
RUN mkdir etl
COPY . modbus/
RUN pip install modbus_tk
CMD python modbus/modbus-server.py