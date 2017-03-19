package fragmentManager;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.idiancan.R;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pictuerManager.FileUitlity;
import pictuerManager.RoundImageView;
import popMenu.PopMenu;
import popMenu.UserMenu;

public class SettingFragment extends Fragment {

    private static final int USER_SEARCH = 0;
    private static final int USER_ADD = 1;
    private static final int USER_CANCEL = 2;

    /**
     * 头像上传，使用系统裁剪
     * 返回固定参数
     * activity返回的参数
     * REQUEST_CODE：裁剪完成的图片
     * ALL_PHOTO：调用手机相册时的参数
     * RESULT_PHOTO：调用手机拍照时的参数
     */
    private static final int REQUEST_CODE = 0x01;
    private static final int ALL_PHOTO = 0x10;
    private static final int RESULT_PHOTO = 0x11;

    /**
     * 使用butterknife
     * 引入的控件
     * 相当于findviewby(id)
     */
    //上传头像的roundimageview
    @Bind(R.id.upLoad)
    RoundImageView upLoad;

    private UserMenu mMenu;

    /**
     * 头像的保存路径
     */
    String path;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View settingLayout = inflater.inflate(R.layout.me_layout,
                container, false);
        /**
         * 初始化popmenu
         */
        initMenu();

        ButterKnife.bind(this, settingLayout);
        return settingLayout;

    }

    private void initMenu() {
        Log.e("********************", "initMenu: " + getActivity().toString());
        mMenu = new UserMenu(getActivity());
        mMenu.addItem(R.string.user_search, USER_SEARCH);
        mMenu.addItem(R.string.user_add, USER_ADD);
        mMenu.addItem(R.string.user_cancel,USER_CANCEL);
        mMenu.setOnItemSelectedListener(new PopMenu.OnItemSelectedListener() {
            @Override
            public void selected(View view, PopMenu.Item item, int position) {
                switch (item.id) {
                    case USER_SEARCH:
                        /**
                         * 搜索，表意调用手机相册进行搜索
                         */
                        searchPhoto();
                        //调用系统裁剪
                        break;
                    case USER_ADD:
                        /**
                         * 添加，表意利用手机相机照相进行添加
                         */
                        takePhoto();
                        break;
                    case USER_CANCEL:
                        /**
                         * 取消操作
                         */
                        break;
                }
            }
        });
    }

    public void toast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * 调用手机相册
     */
    private void searchPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,ALL_PHOTO);
    }

    /**
     * 调用手机拍照
     */
    private void takePhoto(){
        //调用手机照相机的方法,通过Intent调用系统相机完成拍照，并调用系统裁剪器裁剪照片
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //创建文件路径,头像保存的路径
        File file = FileUitlity.getInstance(getActivity()).makeDir("head_image");
        //定义图片路径和名称
        path = file.getParent() + File.separatorChar + System.currentTimeMillis() + ".jpg";
        //保存图片到Intent中，并通过Intent将照片传给系统裁剪器
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(path)));
        //图片质量
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        //启动有返回的Intent，即返回裁剪好的图片到RoundImageView组件中显示
        startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * 上传头像按钮的点击事件
     */
    @OnClick(R.id.upLoad)
    public void onClick() {
        View view =View.inflate(getActivity(),R.layout.menu_user_1,null);
        mMenu.showAsDropDown(view);
    }

    //调用系统裁剪的方法
    private void startPhoneZoom(Uri uri){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //是否可裁剪
        intent.putExtra("corp", "true");
        //裁剪器高宽比
        intent.putExtra("aspectY",1);
        intent.putExtra("aspectX",1);
        //设置裁剪框高宽
        intent.putExtra("outputX",150);
        intent.putExtra("outputY", 150);
        //返回数据
        intent.putExtra("return-data",true);
        startActivityForResult(intent,RESULT_PHOTO);
    }

    //该方法实现通过何种方式跟换图片
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //如果返回码不为-1，则表示不成功
        if (resultCode != Activity.RESULT_OK){
            return;
        }
        if (requestCode == ALL_PHOTO){
            //调用相册
            Cursor cursor = getActivity().getContentResolver().query(data.getData(),
                    new String[]{MediaStore.Images.Media.DATA},null,null,null);
            //游标移到第一位，即从第一位开始读取
            cursor.moveToFirst();
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
            //调用系统裁剪
            startPhoneZoom(Uri.fromFile(new File(path)));
        }else if (requestCode == REQUEST_CODE){
            //相机返回结果，调用系统裁剪
            startPhoneZoom(Uri.fromFile(new File(path)));
        }else if(requestCode == RESULT_PHOTO) {
            //设置裁剪返回的位图
            Bundle bundle = data.getExtras();
            if (bundle!=null){
                Bitmap bitmap = bundle.getParcelable("data");
                //将裁剪后得到的位图在组件中显示
                upLoad.setImageBitmap(bitmap);
            }
        }
    }
}
