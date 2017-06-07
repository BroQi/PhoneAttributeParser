package cn.broqi.parser.controller;

import cn.broqi.parser.entity.NumAttribution;
import cn.broqi.parser.service.NumAttributionService;
import cn.broqi.parser.util.DateUtil;
import org.apache.commons.io.FileUtils;
import org.apache.poi.poifs.filesystem.DocumentFactoryHelper;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DecimalFormat;
import java.util.Date;

@RestController
@RequestMapping("/parse")
public class ParserController extends BaseController {

    private static Logger log = LoggerFactory.getLogger(ParserController.class);

    @Autowired
    private NumAttributionService numAttributionService;

    @Value("${tmp.parse.excel.dir}")
    private String saveExcelDir;

    /**
     * 手机号正则表达式
     */
    private static final String REGEX_PHONE_NUMBER = "^1[34578]\\d{9}$";

    /**
     * 上传并解析EXCEL文件
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

        if (!fileType.equalsIgnoreCase("xls") && !fileType.equalsIgnoreCase("xlsx")) {
            return super.getFailMsg("文件格式不正确");
        }
        InputStream is = null;
        try {
            is = file.getInputStream();
            if (!is.markSupported()) {
                is = new PushbackInputStream(is, 8);
            }
            if (!POIFSFileSystem.hasPOIFSHeader(is) && !DocumentFactoryHelper.hasOOXMLHeader(is)) {
                log.debug("不能解析的文件格式");
                return super.getFailMsg("文件格式无法解析");
            }
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);
            Row titleRow = sheet.getRow(0);
            titleRow.createCell(1).setCellValue("省份");
            titleRow.createCell(2).setCellValue("城市");
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                Cell phone = row.getCell(0);
                if (phone == null) {
                    continue;
                }
                String phoneNumber = this.getValueFromExcel(phone);
                NumAttribution numberAttr = this.getCity(phoneNumber);
                row.createCell(1).setCellValue(numberAttr.getProvince());
                row.createCell(2).setCellValue(numberAttr.getCity());
            }
            String downFileName = fileName + "_已处理_" + DateUtil.dateToString(new Date()) + ".xlsx";
            File saveDir = new File(this.saveExcelDir);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            File saveFile = new File(this.saveExcelDir + File.separator + downFileName);
            OutputStream out = new FileOutputStream(saveFile);
            workbook.write(out);
            out.flush();
            out.close();
            return super.getSuccessMsg((Object) downFileName);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("EXCEL处理出现错误" + e);
            return super.getFailMsg("EXCEL处理出现错误");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 下载文件
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "download/{fileName:.*}", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<byte[]> download(@PathVariable() String fileName) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);
        File file = new File(this.saveExcelDir + fileName);
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
    }

    /**
     * 读取EXCEL单元格值为String
     *
     * @param hssfCell
     * @return
     */
    private String getValueFromExcel(Cell hssfCell) {
        if (hssfCell.getCellTypeEnum() == CellType.BOOLEAN) {
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellTypeEnum() == CellType.NUMERIC) {
            return new DecimalFormat("0").format(hssfCell.getNumericCellValue());
        } else {
            return String.valueOf(hssfCell.getStringCellValue());
        }
    }

    /**
     * 获取归属地信息
     *
     * @param phone
     * @return
     */
    public NumAttribution getCity(String phone) {
        NumAttribution num = null;
        if (phone.matches(REGEX_PHONE_NUMBER) && (num = numAttributionService.findByNum(phone)) != null) {
            String city = num.getCity();
            if (city.endsWith("市")) {
                city = city.substring(0, city.length() - 1);
            }
            num.setCity(city);
        } else {
            num = new NumAttribution("未知", "未知");
        }
        return num;
    }

}
