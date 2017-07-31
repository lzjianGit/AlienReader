package org.java.reader;

import com.alien.uhf.MinaSerialSingle;
import com.alien.uhf.core.FilterParam;
import com.alien.uhf.core.FrameDataBuilder;
import com.alien.uhf.core.FrameDataResponse;
import com.alien.uhf.core.HandleResponse;
import com.alien.uhf.core.HandlerResult;
import com.alien.uhf.core.ReadWriteParam;
import com.alien.uhf.exception.FrameDataErrorException;
import com.alien.uhf.utils.HexUtil;

public class ReadWriteTest {

	MinaSerialSingle minaSerialSingle = MinaSerialSingle.getInstance();
	
	public void before() {

		try {
			minaSerialSingle.openPort("COM3", 115200);
		} catch (com.alien.uhf.exception.NoSuchPort e) {
			System.out.println(e);
			minaSerialSingle.close();
			return;
		} catch (com.alien.uhf.exception.PortInUse e) {
			System.out.println(e);
			minaSerialSingle.close();
			return;
		}
	}

	public void First_ReadTID() {
		String TID = null;
		// 标签 访问密码
		byte[] AccPwd = HexUtil.hexStringToBytes("00000000");

		// 读取TID参数 设置读取的 memory bank ，起始地址 （字节） ，长度（字节）
		ReadWriteParam readTID = new ReadWriteParam(ReadWriteParam.MB_TID, ReadWriteParam.TID_START,
				ReadWriteParam.TID_LEN);
		// 设置过滤为false
		boolean filter = false;
		// 构建读取数据命令
		byte[] sendBuf = null;
		try {
			sendBuf = FrameDataBuilder.readData(AccPwd, filter, null, readTID);
			// 发送数据 获得响应 设置超时时间 200 毫秒 暂不支持连续寻签的处理
			FrameDataResponse res = minaSerialSingle.send(sendBuf, 200);
			// 如果响应成功 处理响应结果
			if (res.isSuccess()) {
				// 自定义响应结果处理 若方法内部无定义
				HandlerResult resTID = HandleResponse.handle(res);
				if (resTID.isSuccess()) {
					TID = (String) resTID.getData();
					System.out.println("TID :" + TID);
				}

			}
		} catch (FrameDataErrorException e) {
			e.printStackTrace();
		}
	}

	public void Second_ReadEPC() {
		String EPC = null;
		// 设置访问密码
		byte[] AccPwd = HexUtil.hexStringToBytes("00000000");

		// 设置读取参数 读取EPC
		ReadWriteParam readEPC = new ReadWriteParam(ReadWriteParam.MB_EPC, ReadWriteParam.EPC_START,
				ReadWriteParam.EPC_LEN);

		// 设置过滤模式
		boolean filter = true;
		// 设置要过滤的TID 取上个方法的TID
		byte[] mDATA = HexUtil.hexStringToBytes("012D0101");
		// 设置过滤TID的参数
		FilterParam filterParam = new FilterParam(FilterParam.MMB_TID, FilterParam.TID_START, FilterParam.TID_LEN,
				mDATA);
		// 发送数据 获得响应 设置超时时间 200 毫秒 暂不支持连续寻签的处理
		byte[] sendBuf;
		try {
			sendBuf = FrameDataBuilder.readData(AccPwd, filter, filterParam, readEPC);
			FrameDataResponse res = minaSerialSingle.send(sendBuf, 200);
			if (res.isSuccess()) {
				// 自定义响应结果处理
				HandlerResult resEPC = HandleResponse.handle(res);
				if (resEPC.isSuccess()) {
					EPC = (String) resEPC.getData();
					System.out.println("EPC :" + EPC);
				}
			}
		} catch (FrameDataErrorException e) {
			e.printStackTrace();
		}
	}

	public void Third_WriteEPC() {
		boolean result = false;
		// 设置访问密码
		byte[] AccPwd = HexUtil.hexStringToBytes("00000000");

		// 设置写入参数 写入EPC
		ReadWriteParam writeEPC = new ReadWriteParam(ReadWriteParam.MB_EPC, ReadWriteParam.EPC_START,
				ReadWriteParam.EPC_LEN, "E20051002307013519304CAA11EFA578");

		// 设置过滤模式
		boolean filter = true;
		// 设置要过滤的TID 取上个方法的TID
		byte[] mDATA = HexUtil.hexStringToBytes("012D0101");
		// 设置过滤TID的参数
		FilterParam filterParam = new FilterParam(FilterParam.MMB_TID, FilterParam.TID_START, FilterParam.TID_LEN,
				mDATA);
		// 发送数据 获得响应 设置超时时间 200 毫秒 暂不支持连续寻签的处理
		byte[] sendBuf;
		try {
			sendBuf = FrameDataBuilder.writeData(AccPwd, filter, filterParam, writeEPC);
			FrameDataResponse res = minaSerialSingle.send(sendBuf, 200);
			if (res.isSuccess()) {
				// 自定义响应结果处理
				HandlerResult resEPC = HandleResponse.handle(res);
				result = resEPC.isSuccess();
				System.out.println("write :" + resEPC);

			}
		} catch (FrameDataErrorException e) {
			e.printStackTrace();
		}
	}

	public void after() {
		minaSerialSingle.close();
	}

}
