FROM python
RUN pip install modbus_tk
RUN mkdir app
COPY . app
CMD PYTHONPATH='./app' modbus-server.py