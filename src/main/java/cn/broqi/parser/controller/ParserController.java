package cn.broqi.parser.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.text.DecimalFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.poi.poifs.filesystem.DocumentFactoryHelper;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.broqi.parser.entity.NumAttribution;
import cn.broqi.parser.service.NumAttributionService;
import cn.broqi.parser.util.DateUtil;

@RestController
@RequestMapping("/parse")
public class ParserController extends BaseController {

	private static Logger log = LoggerFactory.getLogger(ParserController.class);

	@Autowired
	private NumAttributionService numAttributionService;
	
	@Value("${tmp.parse.excel.dir}")
	private String saveExcelDir;

	/**
	 * �ϴ��ļ������н���
	 * 
	 * @param file
	 * @param email
	 * @return
	 */
	@SuppressWarnings("resource")
	@RequestMapping(consumes = "multipart/form-data", method = RequestMethod.POST)
	public ModelMap parse(@RequestParam("file") MultipartFile file, String email, HttpServletResponse response) {
		String fileName = file.getOriginalFilename();
		String fileType = null;
		if (fileName.indexOf('.') >= 0) {
			fileType = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
			fileName = fileName.substring(0, fileName.lastIndexOf("."));
		}

		if (!fileType.equals("xls") && !fileType.equals("xlsx")) {
			return super.getFailMsg("文件格式不正确");
		}
		InputStream is;
		try {
			is = file.getInputStream();
			if (!is.markSupported()) {
				is = new PushbackInputStream(is, 8);
			}
			if (!POIFSFileSystem.hasPOIFSHeader(is) && !DocumentFactoryHelper.hasOOXMLHeader(is)) {
				log.debug("不能解析的文件格式");
				return super.getFailMsg("getInputStreamʽ");
			}
			XSSFWorkbook hssfWorkbook = new XSSFWorkbook(is);
			XSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
			XSSFRow titleRow = hssfSheet.getRow(0);
			titleRow.createCell(1).setCellValue("省份");
			titleRow.createCell(2).setCellValue("城市");
			for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
				XSSFRow hssfRow = hssfSheet.getRow(rowNum);
				if (hssfRow == null) {
					continue;
				}
				System.out.println("----------第" + rowNum + "条---------\n");
				XSSFCell phone = hssfRow.getCell(0);
				if (phone == null) {
					continue;
				}
				String phoneNumber = this.getValueFromExcel(phone);
				NumAttribution numberAttr = this.getCity(phoneNumber);
				hssfRow.createCell(1).setCellValue(numberAttr.getProvince());
				hssfRow.createCell(2).setCellValue(numberAttr.getCity());
				System.out.println("\t手机号----- ：" + phoneNumber + " -----");
			}
			// ���
			String downFileName = fileName + "_已处理_" + DateUtil.dateToString(new Date()) + ".xlsx";
			File saveDir = new File(this.saveExcelDir);
			if (!saveDir.exists()) {
				saveDir.mkdirs();
			}
			File saveFile = new File(this.saveExcelDir + File.separator + downFileName);
			OutputStream out = new FileOutputStream(saveFile);
			hssfWorkbook.write(out);
			out.flush();
			out.close();
			return super.getSuccessMsg((Object) downFileName);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("EXCEL处理出现错误" + e);
			return super.getFailMsg("EXCEL处理出现错误");
		}
	}
	
	/**
	 * 下载文件
	 * @param fileName
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "download/{fileName:.*}", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<byte[]> download(@PathVariable() String fileName) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("attachment", fileName);
		File file = new File(this.saveExcelDir + fileName);
		return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
	}

	private String getValueFromExcel(XSSFCell hssfCell) {
		if (hssfCell.getCellTypeEnum() == CellType.BOOLEAN) {
			// ���ز������͵�ֵ
			return String.valueOf(hssfCell.getBooleanCellValue());
		} else if (hssfCell.getCellTypeEnum() == CellType.NUMERIC) {
			// ������ֵ���͵�ֵ
			return new DecimalFormat("0").format(hssfCell.getNumericCellValue());
		} else {
			// �����ַ������͵�ֵ
			return String.valueOf(hssfCell.getStringCellValue());
		}
	}

	public NumAttribution getCity(String phone) {
		NumAttribution num = numAttributionService.findByNum(phone);
		if (num != null) {
			String city = num.getCity();
			if (city.endsWith("市"))
				city = city.substring(0, city.length() - 1);
			num.setCity(city);
		} else {
			num = new NumAttribution("未知", "未知");
		}
		return num;
	}

}
