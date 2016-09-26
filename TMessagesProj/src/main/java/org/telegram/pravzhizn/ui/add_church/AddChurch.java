package org.telegram.pravzhizn.ui.add_church;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.techranch.R;
import org.telegram.pravzhizn.ui.photos.PhotosListActivity;
import org.telegram.pravzhizn.pravzhizn.PravzhiznService;
import org.telegram.pravzhizn.utils.FileUploadHelper;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.PhotoAlbumPickerActivity;
import org.telegram.ui.PhotoViewer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.telegram.pravzhizn.analytics.AnalyticsUtils.trackScreenName;
import static org.telegram.pravzhizn.utils.PhotosCount.photosLabelText;

/**
 * Created by vlad on 5/29/16.
 */
public class AddChurch extends BaseFragment {

    private static final int PICK_PHOTO_FROM_CAMERA = 13;
    private static final int PICK_PHOTO_FROM_GALLERY = 14;
    private static final int MAX_PHOTOS_AVAILABLE_FOR_UPLOAD = 3;
    private static final String SCREEN_NAME = "Add church";

    private ImageView mAlbumViewHeader;
    private ImageView mAddPhotosButton;
    private TextView mPhotosLabel;
    private Button mAddChurchButton;
    private EditText mChurchName;
    private EditText mChurchAddress;

    private List<BitmapWithPath> mPhotoslist = new ArrayList<>();
    private String mCurrentPicturePath;
    private PravzhiznService mService;

    public class BitmapWithPath {

        private final Bitmap mBitmap;
        private final String mPath;

        private BitmapWithPath(final Bitmap bitmap, final String path) {
            mBitmap = bitmap;
            mPath = path;
        }

        public Bitmap getBitmap() {
            return mBitmap;
        }

        public String getPath() {
            return mPath;
        }
    }

    @Override
    public boolean onFragmentCreate() {
        trackScreenName(SCREEN_NAME);
        return super.onFragmentCreate();
    }

    @Override
    public View createView(final Context context) {
        Theme.loadRecources(context);

        mService = PravzhiznService.instance.create(PravzhiznService.class);

        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("Techranch_Add_Church", R.string.Techranch_Add_Church));

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        fragmentView = LayoutInflater.from(context).inflate(R.layout.techranch_add_church, null);;

        mAlbumViewHeader = (ImageView) fragmentView.findViewById(R.id.techranch_church_album_view);
        mAlbumViewHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (!mPhotoslist.isEmpty()) {
                    PhotosListActivity activity = new PhotosListActivity(mPhotoslist);
                    presentFragment(activity);
                }
            }
        });
        mAddPhotosButton = (ImageView) fragmentView.findViewById(R.id.add_photos);
        mAddPhotosButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (getParentActivity() == null) {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());

                CharSequence[] items = new CharSequence[]{LocaleController.getString("FromCamera", R.string.FromCamera), LocaleController.getString("FromGalley", R.string.FromGalley)};
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mPhotoslist.size() >= MAX_PHOTOS_AVAILABLE_FOR_UPLOAD) {

                            final String message = LocaleController.getString("techranch_photos_limit_reached", R.string.techranch_photos_limit_reached);
                            Toast.makeText(getParentActivity(), message, Toast.LENGTH_SHORT).show();

                        } else if (i == 0) {
                            openCamera();
                        } else if (i == 1) {
                            openGallery();
                        }
                    }
                });
                showDialog(builder.create());
            }
        });

        mPhotosLabel = (TextView) fragmentView.findViewById(R.id.techranch_photos_label);
        updatePhotosLabelText();

        mAddChurchButton = (Button) fragmentView.findViewById(R.id.techranch_add_church);
        mAddChurchButton.setText(LocaleController.getString("Techranch_Add_Church_Button", R.string.Techranch_Add_Church_Button));
        mAddChurchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                handleAddChurchButtonClick(context);
            }
        });

        mChurchName = (EditText) fragmentView.findViewById(R.id.techranch_church_name);
        mChurchName.setHint(LocaleController.getString("Techranch_Church_To_Add_Name", R.string.Techranch_Church_To_Add_Name));

        mChurchAddress = (EditText) fragmentView.findViewById(R.id.techranch_church_address);
        mChurchAddress.setHint(LocaleController.getString("Techranch_Church_To_Add_Address", R.string.Techranch_Church_To_Add_Address));
        mChurchAddress.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(final TextView textView, final int actionId, final KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    handleAddChurchButtonClick(context);
                    return true;
                }
                return false;
            }
        });

        return fragmentView;
    }

    void handleAddChurchButtonClick(final Context context) {
        if (isRequiredInformationFilled()) {
            String name = mChurchName.getText().toString();
            String address = mChurchAddress.getText().toString();
            TLRPC.User user = UserConfig.getCurrentUser();

            RequestBody namePart = FileUploadHelper.createPartFromString(name);
            RequestBody addressPart = FileUploadHelper.createPartFromString(address);

            MultipartBody.Part body1;
            MultipartBody.Part body2;
            MultipartBody.Part body3;
            Call<ResponseBody> responseBodyCall;
            switch (mPhotoslist.size()) {
                case 0:
                    responseBodyCall = mService.createTempleRequest(namePart, addressPart);
                    break;
                case 1:
                    body1 = FileUploadHelper.prepareFilePart(getParentActivity(), "image1", mPhotoslist.get(0).getPath());
                    responseBodyCall = mService.createTempleRequest(namePart, addressPart, body1);
                    break;
                case 2:
                    body1 = FileUploadHelper.prepareFilePart(getParentActivity(), "image1", mPhotoslist.get(0).getPath());
                    body2 = FileUploadHelper.prepareFilePart(getParentActivity(), "image2", mPhotoslist.get(1).getPath());
                    responseBodyCall = mService.createTempleRequest(namePart, addressPart, body1, body2);
                    break;
                case 3:
                default:
                    body1 = FileUploadHelper.prepareFilePart(getParentActivity(), "image1", mPhotoslist.get(0).getPath());
                    body2 = FileUploadHelper.prepareFilePart(getParentActivity(), "image2", mPhotoslist.get(1).getPath());
                    body3 = FileUploadHelper.prepareFilePart(getParentActivity(), "image3", mPhotoslist.get(2).getPath());
                    responseBodyCall = mService.createTempleRequest(namePart, addressPart, body1, body2, body3);
                    break;
            }

            ProgressDialog progressDialog = null;
            if (context != null) {
                try {
                    progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage(LocaleController.getString("techranch_sending_request", R.string.techranch_sending_request));
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.show();
                } catch (Exception e) {
                    FileLog.e("tmessages", e);
                }
            }

            final ProgressDialog finalProgressDialog = progressDialog;
            responseBodyCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(final Call<ResponseBody> call, final Response<ResponseBody> response) {
                    if (finalProgressDialog != null) {
                        finalProgressDialog.dismiss();
                    }

                    String message = LocaleController.getString("techranch_send_request_to_add_temple_clicked", R.string.techranch_send_request_to_add_temple_clicked);
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    finishFragment(true);
                }

                @Override
                public void onFailure(final Call<ResponseBody> call, final Throwable t) {
                    if (finalProgressDialog != null) {
                        finalProgressDialog.dismiss();
                    }

                    Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                }
            });

        } else {
            final String message = LocaleController.getString("Techranch_Fill_All_Fields", R.string.Techranch_Fill_All_Fields);
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

    void updatePhotosLabelText() {
        this.mPhotosLabel.setText(photosLabelText(mPhotoslist));
    }

    private boolean isRequiredInformationFilled() {
        final boolean isThereAnyName = !TextUtils.isEmpty(mChurchName.getText());
        final boolean isThereAnyDescription = !TextUtils.isEmpty(mChurchAddress.getText());
        return isThereAnyName && isThereAnyDescription;
    }

    @Override
    public void onResume() {
        super.onResume();

        updatePhotosLabelText();
        if (!mPhotoslist.isEmpty()) {
            mAlbumViewHeader.setImageBitmap(mPhotoslist.get(0).getBitmap());
        } else {
            mAlbumViewHeader.setImageResource(R.drawable.img_gag_tample);
        }
    }

    public void openCamera() {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File image = AndroidUtilities.generatePicturePath();
            if (image != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
                mCurrentPicturePath = image.getAbsolutePath();
            }
            startActivityForResult(takePictureIntent, PICK_PHOTO_FROM_CAMERA);
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }
    }

    public void openGallery() {
        if (Build.VERSION.SDK_INT >= 23 && getParentActivity() != null) {
            if (getParentActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                getParentActivity().requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 4);
                return;
            }
        }
        PhotoAlbumPickerActivity fragment = new PhotoAlbumPickerActivity(true, false, null);
        fragment.setDelegate(new PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate() {
            @Override
            public void didSelectPhotos(ArrayList<String> photos, ArrayList<String> captions, ArrayList<MediaController.SearchImage> webPhotos) {
                if (!photos.isEmpty()) {
                    final String path = photos.get(0);
                    Bitmap bitmap = ImageLoader.loadBitmap(path, null, 800, 800, true);
                    addBitmapToList(bitmap, path);
                }
            }

            @Override
            public void startPhotoSelectActivity() {
                try {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, PICK_PHOTO_FROM_GALLERY);
                } catch (Exception e) {
                    FileLog.e("tmessages", e);
                }
            }

            @Override
            public boolean didSelectVideo(String path) {
                return true;
            }
        });
        presentFragment(fragment);
    }

    @Override
    public void onActivityResultFragment(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResultFragment(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_PHOTO_FROM_CAMERA) {
                PhotoViewer.getInstance().setParentActivity(getParentActivity());

                final String pathOfPhoto = mCurrentPicturePath;
                Glide.with(getParentActivity())
                        .load(pathOfPhoto)
                        .into(new SimpleTarget<GlideDrawable>() {
                            @Override
                            public void onResourceReady(final GlideDrawable resource, final GlideAnimation<? super GlideDrawable> glideAnimation) {
                                addBitmapToList(resource, pathOfPhoto);
                            }
                        });

                mCurrentPicturePath = null;
            }
            if (requestCode == PICK_PHOTO_FROM_GALLERY) {
                if (data == null) {
                    return;
                }
                try {
                    Uri selectedImage = data.getData();
                    InputStream inputStream = getParentActivity().getContentResolver().openInputStream(data.getData());
                    addBitmapToList(BitmapFactory.decodeStream(inputStream), selectedImage.getPath());
                } catch (FileNotFoundException e) {
                    FileLog.e("tmessages", e);
                }
            }
        }
    }

    void addBitmapToList(final GlideDrawable resource, final String picturePath) {
        addBitmapToList(drawableToBitmap(resource), picturePath);
    }

    void addBitmapToList(final Bitmap bitmap, final String picturePath) {
        mPhotoslist.add(new BitmapWithPath(bitmap, picturePath));
        mAlbumViewHeader.setImageBitmap(bitmap);
        updatePhotosLabelText();
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}
