package com.sunmi.sunmit2demo.fragment;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.appcompat.widget.AppCompatSpinner;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sunmi.sunmit2demo.BaseFragment;
import com.sunmi.sunmit2demo.R;
import com.sunmi.sunmit2demo.bean.GoodsCode;
import com.sunmi.sunmit2demo.bean.GvBeans;
import com.sunmi.sunmit2demo.presenter.ProductPresenter;
import com.sunmi.sunmit2demo.ui.MoreActivity;
import com.sunmi.sunmit2demo.utils.ResourcesUtils;
import com.sunmi.sunmit2demo.view.CustomCarGoodsCounterView;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class BackgroundManagerFragment extends BaseFragment implements View.OnClickListener {

    private ImageView ivIcon;
    private TextView tvIdTitle;
    private TextView tvId;
    private EditText etName;
    private EditText etPrice;
    private EditText etUnit;
    private CustomCarGoodsCounterView etNum;
    private Button btnAdd;
    private Button btnCancel;
    private Button btnDelete;
    private AppCompatSpinner spinner;
    String photoPath;
    boolean isUpdate;

    GvBeans gvBeans;
    int mode = GoodsCode.MODE_0;

    @Override
    protected int setView() {
        return R.layout.fragment_background_setting;
    }

    @Override
    protected void init(View view) {

        ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
        tvIdTitle = (TextView) view.findViewById(R.id.tv_id_title);
        tvId = (TextView) view.findViewById(R.id.tv_id);
        etName = (EditText) view.findViewById(R.id.et_name);
        etPrice = (EditText) view.findViewById(R.id.et_price);
        etUnit = (EditText) view.findViewById(R.id.et_unit);
        etNum = (CustomCarGoodsCounterView) view.findViewById(R.id.et_num);
        btnAdd = (Button) view.findViewById(R.id.btn_add);
        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnDelete = (Button) view.findViewById(R.id.btn_delete);
        spinner = view.findViewById(R.id.sp_pay_mode);
        etName.setSaveEnabled(false);
        etPrice.setSaveEnabled(false);
        etUnit.setSaveEnabled(false);
        etNum.setSaveEnabled(false);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mode = GoodsCode.MODE_0;
                        break;
                    case 1:
                        mode = GoodsCode.MODE_1;
                        break;
                    case 2:
                        mode = GoodsCode.MODE_5;
                        break;
                    case 3:
                        mode = GoodsCode.MODE_6;
                        break;
                    case 4:
                        mode = GoodsCode.MODE_7;
                        break;
                    case 5:
                        mode = GoodsCode.MODE_8;
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        String id = getArguments().getString("id").trim();
        if (TextUtils.isEmpty(getArguments().getString("id"))) {
            finish();
            return;
        }
        tvId.setText(id);
        gvBeans = ProductPresenter.getInstance().getProductByCode(id);

        if (gvBeans != null) {
            isUpdate = true;
            etPrice.setText(gvBeans.getPrice().substring(1));
            etName.setText(gvBeans.getName());
            etUnit.setText(gvBeans.getUnit());
            etNum.setGoodsNumber(gvBeans.getNumber());
            mode = gvBeans.getMode();
            switch (mode) {
                case GoodsCode.MODE_0:
                    spinner.setSelection(0);
                    break;
                case GoodsCode.MODE_1:
                    spinner.setSelection(1);
                    break;
                case GoodsCode.MODE_5:
                    spinner.setSelection(2);
                    break;
                case GoodsCode.MODE_6:
                    spinner.setSelection(3);
                    break;
                case GoodsCode.MODE_7:
                    spinner.setSelection(4);
                    break;
                case GoodsCode.MODE_8:
                    spinner.setSelection(5);
                    break;
                default:
            }
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            isUpdate = false;
            gvBeans = new GvBeans();
            etPrice.setText("");
            etName.setText("");
            etUnit.setText("");
            etNum.setGoodsNumber(1);
            btnDelete.setVisibility(View.GONE);
        }

        //设置输入框允许输入的类型（正则）

        etPrice.setKeyListener(DigitsKeyListener.getInstance("0123456789."));

        //设置输入字符

        etPrice.setFilters(new InputFilter[]{inputFilter});

        ivIcon.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    private InputFilter inputFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            // 删除等特殊字符，直接返回
            if (TextUtils.isEmpty(source)) {
                return null;
            }
            String dValue = dest.toString();
            String[] splitArray = dValue.split("\\.");
            if (splitArray.length > 1) {
                String dotValue = splitArray[1];
                int dotIndex = dValue.indexOf(".");
                if (dend <= dotIndex) {
                    return null;
                } else {
                    // 2 表示输入框的小数位数
                    int diff = dotValue.length() + 1 - 2;
                    if (diff > 0) {
                        return source.subSequence(start, end - diff);
                    }
                }
            }
            return null;
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_delete:
                ProductPresenter.getInstance().delectProduct(gvBeans);
                Toast.makeText(getContext(), "删除成功", Toast.LENGTH_LONG).show();
                finish();
                break;
            case R.id.iv_icon:
                goPhotoAlbum();
                break;
            case R.id.btn_add:
                if (isUpdate) {
                    update();
                } else {
                    add();
                }
                finish();
                break;

            case R.id.btn_cancel:
                finish();
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK) {
            photoPath = getRealPathFromUri(getContext(), data.getData());
            ivIcon.setImageBitmap(getDiskBitmap(photoPath));
            Log.e("@@@", "onActivityResult==" + photoPath);
        }
    }

    private void finish() {
        ((MoreActivity) getActivity()).replaceContent(((MoreActivity) getActivity()).goodsManagerFragment, false);
    }

    private void update() {
        String id = tvId.getText().toString();
        String url = photoPath;
        String name = etName.getText().toString();
        String price = ResourcesUtils.getString(R.string.units_money) + etPrice.getText().toString();
        String unit = etUnit.getText().toString();
        int num = etNum.getGoodsNumber();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(price)) {
            Toast.makeText(getContext(), "请填入必填", Toast.LENGTH_LONG).show();
            return;
        }

        gvBeans.setName(name);
        gvBeans.setPrice(price);
        gvBeans.setUnit(unit);
        gvBeans.setNumber(num);
        gvBeans.setImgUrl(url);
        gvBeans.setMode(mode);
        ProductPresenter.getInstance().addProduct(gvBeans);
        Toast.makeText(getContext(), "保存成功", Toast.LENGTH_LONG).show();


    }

    private void add() {
        String id = tvId.getText().toString();
        String url = photoPath;
        String name = etName.getText().toString();
        String price = ResourcesUtils.getString(R.string.units_money) + etPrice.getText().toString();
        String unit = etUnit.getText().toString();
        int num = etNum.getGoodsNumber();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(price)) {
            Toast.makeText(getContext(), "请填入必填", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(url)) {
            gvBeans.setImgId(R.drawable.ic_launcher);
        }
        gvBeans.setName(name);
        gvBeans.setPrice(price);
        gvBeans.setUnit(unit);
        gvBeans.setNumber(num);
        gvBeans.setImgUrl(url);
        gvBeans.setMode(mode);
        gvBeans.setCode(id);

        ProductPresenter.getInstance().addProduct(gvBeans);
        Toast.makeText(getContext(), "保存成功", Toast.LENGTH_LONG).show();

    }

    /**
     * 加载本地图片
     * http://bbs.3gstdy.com
     *
     * @param pathString
     * @return
     */
    private Bitmap getDiskBitmap(String pathString) {
        Bitmap bitmap = null;
        try {
            File file = new File(pathString);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(pathString);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }


        return bitmap;
    }

    //激活相册操作

    private void goPhotoAlbum() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }

    /**
     * 根据Uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    public static String getRealPathFromUri(Context context, Uri uri) {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= 19) {
            return getRealPathFromUriAboveApi19(context, uri);
        } else {
            return getRealPathFromUriBelowAPI19(context, uri);
        }
    }

    /**
     * 适配api19以下(不包括api19),根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    private static String getRealPathFromUriBelowAPI19(Context context, Uri uri) {
        return getDataColumn(context, uri, null, null);
    }

    /**
     * 适配api19及以上,根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    private static String getRealPathFromUriAboveApi19(Context context, Uri uri) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的 uri, 则通过document id来进行处理
            String documentId = DocumentsContract.getDocumentId(uri);
            if (isMediaDocument(uri)) {
                // 使用':'分割
                String id = documentId.split(":")[1];

                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = {id};
                filePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
            } else if (isDownloadsDocument(uri)) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                filePath = getDataColumn(context, contentUri, null, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是 content 类型的 Uri
            filePath = getDataColumn(context, uri, null, null);
        } else if ("file".equals(uri.getScheme())) {
            // 如果是 file 类型的 Uri,直接获取图片对应的路径
            filePath = uri.getPath();
        }
        return filePath;
    }

    /**
     * 获取数据库表中的 _data 列，即返回Uri对应的文件路径
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path = null;

        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is MediaProvider
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is DownloadsProvider
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
}

