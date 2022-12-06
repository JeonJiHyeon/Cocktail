package com.navi_baekgu.ui.recipe;

import static com.google.ar.sceneform.rendering.HeadlessEngineWrapper.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.RenderableDefinition;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.Vertex;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.navi_baekgu.R;
import com.navi_baekgu.ui.recycler.Cocktail;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import cn.pedant.SweetAlert.SweetAlertDialog;

//컨트롤 쉬프트 빼기 : 모든 코드 접기
public class CameraguideActivity extends AppCompatActivity implements
        BaseArFragment.OnTapArPlaneListener {
    //<editor-fold desc="변수 생성구간">
    //최대 앵커 수를 2개로 제한함
    private static final int MAX_ANCHORS = 2;
    //앵커노드리스트를 생성
    private List<AnchorNode> anchorNodeList = new ArrayList<>();
    //지금 몇개의 앵커가 있는지 저장할 변수
    private Integer numberOfAnchors = 0;
    private Integer create_mode = 0;
    //길이정보 저장해줄 변수
    private double cup_height;
    private double cup_width, cup_width_under;
    private double cup_volume;
    private Pose width_pose[], width_pose_under[];
    private Pose height_pose[];
    //지금 선택한 노드를 가르킬것 - height 측정시 사용됨
    private AnchorNode selectedAnchorNode = null;
    private AnchorNode selectedAnchorNode_left = null;
    private AnchorNode selectedAnchorNode_right = null;
    //버튼 생성
    private FloatingActionButton deleteButton;
    private FloatingActionButton heightButton;
    private FloatingActionButton rightButton;
    private FloatingActionButton leftButton;
    private Button mug_cup_btn;
    private Button wine_glass_btn;
    private Button cocktail_glass_btn;
    private Button cancel_btn;
    private Button complete_btn;
    private Button result_;
    private String cupname;
    private boolean cancle;
    private Cocktail selected_cocktail;

    private ArFragment arFragment;
    //컬러와 렌더러블<3D model and consists of vertices, materials, textures, and more.> 생성
    private final Color color = new Color(android.graphics.Color.parseColor("#ff0051"));
    private final Color color2 = new Color(android.graphics.Color.parseColor("#ffdee8"));
    private final Color color3 = new Color(android.graphics.Color.parseColor("#c4d1ff"));
    private Renderable sphere, guide_sphere, under_sphere;
    private ViewRenderable renderable_ui_info, renderable_ui_result;
    private AnchorNode resultanchor;
    private AnchorNode anchorNode_u;

    //</editor-fold desc="변수 생성구간">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cameraguide);
        Intent intent = getIntent();
        selected_cocktail = (Cocktail) intent.getSerializableExtra("selected_cocktail"); // 직렬화된 객체를 받는 방법
        new SweetAlertDialog(CameraguideActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("카메라 가이드에 온 것을 환영합니다.")
                .setContentText("먼저 컵의 모양을 선택한 후, 가이드에 따라 컵을 측정하고 레시피를 안내받으세요!")
                .setConfirmText("확인했습니다")
                .show();
//<editor-fold desc="변수 매칭구간">
        mug_cup_btn = findViewById(R.id.mug_cup_btn);
        wine_glass_btn = findViewById(R.id.wine_glass_btn);
        cocktail_glass_btn = findViewById(R.id.cocktail_glass_btn);
        deleteButton = findViewById(R.id.delete);
        cancel_btn = findViewById(R.id.cancel_btn);
        complete_btn = findViewById(R.id.complete_btn);
        heightButton = findViewById(R.id.height);
        result_ = (Button) findViewById(R.id.flag);
        leftButton = findViewById(R.id.left);
        rightButton = findViewById(R.id.right);
//</editor-fold desc="변수 매칭구간">
        numberOfAnchors = MAX_ANCHORS;
        getSupportFragmentManager().addFragmentOnAttachListener((fragmentManager, fragment) -> {
            if (fragment.getId() == R.id.arFragment) {
                arFragment = (ArFragment) fragment;
                arFragment.setOnTapArPlaneListener(CameraguideActivity.this);
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.arFragment, ArFragment.class, null)
                    .commit();

        }
//<editor-fold desc="렌더러블 초기 설정">
        //Material 색상 지정하고, 그걸로 shapeFactory를 통해 랜더러블 구를 생성함 radius 0.01f
        //모델과 텍스처를 초기화시키고 그것을 계속 재사용하는거랑 같음!!
        MaterialFactory.makeOpaqueWithColor(this, color)
                .thenAccept(material -> {
                    sphere = ShapeFactory.makeSphere(0.015f, Vector3.zero(), material);
                    sphere.setShadowCaster(false);
                });
        MaterialFactory.makeOpaqueWithColor(this, color2)
                .thenAccept(material -> {
                    guide_sphere = ShapeFactory.makeSphere(0.015f, Vector3.zero(), material);
                    sphere.setShadowCaster(false);
                });
        MaterialFactory.makeOpaqueWithColor(this, color3)
                .thenAccept(material -> {
                    under_sphere = ShapeFactory.makeSphere(0.015f, Vector3.zero(), material);
                    sphere.setShadowCaster(false);
                });
        //2d ui를 띄운다. 카메라 유아이라는 xml을 따로 만들어서 뷰 렌더러블 클래스에 적용시킨것. 적용시키는 방법은 위에 있던것들하고 동일
        WeakReference<CameraguideActivity> weakActivity = new WeakReference<>(this);
        ViewRenderable.builder()
                .setView(this, R.layout.camera_ui)
                .build()
                .thenAccept(renderable -> {
                    CameraguideActivity activity = weakActivity.get();
                    if (activity != null) {
                        activity.renderable_ui_info = renderable;
                        renderable_ui_info.setShadowReceiver(false);
                        renderable_ui_info.setShadowCaster(false);
                    }
                }).exceptionally(throwable -> {
                    Toast.makeText(this, "Unable to load ui", Toast.LENGTH_LONG).show();
                    return null;
                });
        ViewRenderable.builder()
                .setView(this, R.layout.camera_ui)
                .build()
                .thenAccept(renderable -> {
                    CameraguideActivity activity = weakActivity.get();
                    if (activity != null) {
                        activity.renderable_ui_result = renderable;
                        renderable_ui_result.setShadowReceiver(false);
                        renderable_ui_result.setShadowCaster(false);
                    }
                }).exceptionally(throwable -> {
                    Toast.makeText(this, "Unable to load ui", Toast.LENGTH_LONG).show();
                    return null;
                });
//</editor-fold desc="렌더러블 초기 설정">
        //머그컵 버튼 클릭리스너
        mug_cup_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancle = false;
                cupname = "mug";
                mug_cup_btn.setVisibility(View.GONE);
                cancel_btn.setVisibility(View.VISIBLE);
                numberOfAnchors = 0;
                create_mode = 1;
                deleteButton.setVisibility(View.VISIBLE);
                wine_glass_btn.setVisibility(View.GONE);
                cocktail_glass_btn.setVisibility(View.GONE);
                result_.setVisibility(View.VISIBLE);
                complete_btn.setVisibility(View.VISIBLE);

                //2d ui로 설명하기
                try {
                    Frame frame = arFragment.getArSceneView().getArFrame();
                    Session session = arFragment.getArSceneView().getSession();
                    Anchor anchor = session.createAnchor(
                            frame.getCamera().getPose()
                                    .compose(Pose.makeTranslation(0, 0, -1f)) //This will place the anchor 1M in front of the camera
                                    .extractTranslation());
                    AnchorNode addedAnchorNode = new AnchorNode(anchor);
                    addedAnchorNode.setEnabled(true);
                    //결과 넣기
                    TextView text = renderable_ui_info.getView().findViewById(R.id.result_text);
                    String msg = "머그컵 측정 안내 - 컵의 너비 측정 후 높이를 측정합니다." +
                            "\n\n너비 측정 방법 : \n\n" +
                            "화면에 보이는 컵의 밑면 지름을 측정하도록 화면을 터치하여 두 점을 설정해주세요." +
                            "\n\n측정이 완료되면, 높이 측정 단계로 넘어갑니다.";
                    text.setText(msg);
                    //닫기 누르면 사라지도록
                    renderable_ui_info.getView().findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addedAnchorNode.setEnabled(false);
                        }
                    });
                    addedAnchorNode.setLocalScale(new Vector3(0.4f, 0.4f, 0.4f));
                    addedAnchorNode.setRenderable(renderable_ui_info);
                    addedAnchorNode.setParent(arFragment.getArSceneView().getScene());
                } catch (Exception e) {
                    new SweetAlertDialog(CameraguideActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("카메라는 컵을 향하게 들어주세요!")
                            .show();
                    mug_cup_btn.setVisibility(View.VISIBLE);
                    wine_glass_btn.setVisibility(View.VISIBLE);
                    cocktail_glass_btn.setVisibility(View.VISIBLE);
                    cancel_btn.setVisibility(View.GONE);
                    result_.setVisibility(View.GONE);
                    complete_btn.setVisibility(View.GONE);
                    numberOfAnchors = MAX_ANCHORS;
                    create_mode = 0;
                    deleteButton.setVisibility(View.GONE);
                }
            }
        });
        wine_glass_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancle = false;
                cupname = "paper";
                mug_cup_btn.setVisibility(View.GONE);
                cancel_btn.setVisibility(View.VISIBLE);
                numberOfAnchors = 0;
                create_mode = 1;
                deleteButton.setVisibility(View.VISIBLE);
                wine_glass_btn.setVisibility(View.GONE);
                cocktail_glass_btn.setVisibility(View.GONE);
                result_.setVisibility(View.VISIBLE);
                complete_btn.setVisibility(View.VISIBLE);

                //2d ui로 설명하기
                try {
                    Frame frame = arFragment.getArSceneView().getArFrame();
                    Session session = arFragment.getArSceneView().getSession();
                    Anchor anchor = session.createAnchor(
                            frame.getCamera().getPose()
                                    .compose(Pose.makeTranslation(0, 0, -1f)) //This will place the anchor 1M in front of the camera
                                    .extractTranslation());
                    AnchorNode addedAnchorNode = new AnchorNode(anchor);
                    addedAnchorNode.setEnabled(true);
                    //결과 넣기
                    TextView text = renderable_ui_info.getView().findViewById(R.id.result_text);
                    String msg = "종이컵형 잔 측정 안내 - 컵의 윗면 너비 측정 후 높이를 측정하고, 그 후 밑면 너비를 측정합니다." +
                            "\n\n윗면 너비 측정 방법 : \n\n" +
                            "화면에 보이는 컵의 밑면 지름을 측정하도록 화면을 터치하여 두 점을 설정해주세요." +
                            "\n\n측정이 완료되면, 높이 측정 단계로 넘어갑니다.";
                    text.setText(msg);
                    //닫기 누르면 사라지도록
                    renderable_ui_info.getView().findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addedAnchorNode.setEnabled(false);
                        }
                    });
                    addedAnchorNode.setLocalScale(new Vector3(0.4f, 0.4f, 0.4f));
                    addedAnchorNode.setRenderable(renderable_ui_info);
                    addedAnchorNode.setParent(arFragment.getArSceneView().getScene());
                } catch (Exception e) {
                    new SweetAlertDialog(CameraguideActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("카메라는 컵을 향하게 들어주세요!")
                            .show();
                    wine_glass_btn.setVisibility(View.VISIBLE);
                    mug_cup_btn.setVisibility(View.VISIBLE);
                    cocktail_glass_btn.setVisibility(View.VISIBLE);
                    cancel_btn.setVisibility(View.GONE);
                    result_.setVisibility(View.GONE);
                    complete_btn.setVisibility(View.GONE);
                    numberOfAnchors = MAX_ANCHORS;
                    create_mode = 0;
                    deleteButton.setVisibility(View.GONE);
                }
            }
        });
        //삼각잔
        cocktail_glass_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancle = false;
                cupname = "cocktail";
                mug_cup_btn.setVisibility(View.GONE);
                cancel_btn.setVisibility(View.VISIBLE);
                numberOfAnchors = 0;
                create_mode = 1;
                deleteButton.setVisibility(View.VISIBLE);
                wine_glass_btn.setVisibility(View.GONE);
                cocktail_glass_btn.setVisibility(View.GONE);
                result_.setVisibility(View.VISIBLE);
                complete_btn.setVisibility(View.VISIBLE);

                //2d ui로 설명하기
                try {
                    Frame frame = arFragment.getArSceneView().getArFrame();
                    Session session = arFragment.getArSceneView().getSession();
                    Anchor anchor = session.createAnchor(
                            frame.getCamera().getPose()
                                    .compose(Pose.makeTranslation(0, 0, -1f)) //This will place the anchor 1M in front of the camera
                                    .extractTranslation());
                    AnchorNode addedAnchorNode = new AnchorNode(anchor);
                    addedAnchorNode.setEnabled(true);
                    //결과 넣기
                    TextView text = renderable_ui_info.getView().findViewById(R.id.result_text);
                    String msg = "삼각형(칵테일) 잔 측정 안내 - 컵의 너비 측정 후 높이를 측정합니다." +
                            "\n\n너비 측정 방법 : \n\n" +
                            "화면에 보이는 컵의 윗면 지름을 측정하도록 화면을 터치하여 두 점을 설정해주세요." +
                            "\n\n측정이 완료되면, 높이 측정 단계로 넘어갑니다.";
                    text.setText(msg);
                    //닫기 누르면 사라지도록
                    renderable_ui_info.getView().findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addedAnchorNode.setEnabled(false);
                        }
                    });
                    addedAnchorNode.setLocalScale(new Vector3(0.4f, 0.4f, 0.4f));
                    addedAnchorNode.setRenderable(renderable_ui_info);
                    addedAnchorNode.setParent(arFragment.getArSceneView().getScene());
                } catch (Exception e) {
                    new SweetAlertDialog(CameraguideActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("카메라는 컵을 향하게 들어주세요!")
                            .show();
                    wine_glass_btn.setVisibility(View.VISIBLE);
                    mug_cup_btn.setVisibility(View.VISIBLE);
                    cocktail_glass_btn.setVisibility(View.VISIBLE);
                    cancel_btn.setVisibility(View.GONE);
                    result_.setVisibility(View.GONE);
                    complete_btn.setVisibility(View.GONE);
                    numberOfAnchors = MAX_ANCHORS;
                    create_mode = 0;
                    deleteButton.setVisibility(View.GONE);
                }
            }
        });
        //완료 버튼 -가이드 시작 버튼- 클릭리스너
        complete_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cup_width <= 0 && cup_height <= 0 && cup_width_under <= 0) {
                    Toast.makeText(CameraguideActivity.this, "길이 측정 완료 후 눌러주세요.", Toast.LENGTH_SHORT).show();
                } else if (cup_height <= 0) {
                    Toast.makeText(CameraguideActivity.this, "높이 측정이 완료되지 않았습니다.", Toast.LENGTH_SHORT).show();
                } else if (cupname.equals("paper")) {
                    if (cup_width_under <= 0) {
                        Log.i("info", "안쪽 이프 문");
                        Toast.makeText(CameraguideActivity.this, "밑면 너비 측정이 완료되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        //모든 ui 없애고 시작
                        renderable_ui_info.getView().findViewById(R.id.close_btn).performClick();
                        renderable_ui_result.getView().findViewById(R.id.close_btn).performClick();
                        //볼륨 계산 필요
                        deleteButton.setVisibility(View.GONE);
                        heightButton.setVisibility(View.GONE);
                        leftButton.setVisibility(View.GONE);
                        rightButton.setVisibility(View.GONE);
                        mug_cup_btn.setVisibility(View.GONE);
                        wine_glass_btn.setVisibility(View.GONE);
                        cocktail_glass_btn.setVisibility(View.GONE);
                        cancel_btn.setVisibility(View.GONE);
                        complete_btn.setVisibility(View.GONE);
                        result_.setVisibility(View.GONE);
                        anchorNode_u.setEnabled(false);
                        while (numberOfAnchors != 0) {
                            deleteButton.performClick();
                        }
                        numberOfAnchors = MAX_ANCHORS;
                        start_guide(cupname, cup_width, cup_height, width_pose, height_pose);
                    }
                } else {
                    //모든 ui 없애고 시작
                    renderable_ui_info.getView().findViewById(R.id.close_btn).performClick();
                    renderable_ui_result.getView().findViewById(R.id.close_btn).performClick();
                    cup_volume = calculateVolume(cup_width / 2, cup_height);
                    deleteButton.setVisibility(View.GONE);
                    heightButton.setVisibility(View.GONE);
                    leftButton.setVisibility(View.GONE);
                    rightButton.setVisibility(View.GONE);
                    mug_cup_btn.setVisibility(View.GONE);
                    wine_glass_btn.setVisibility(View.GONE);
                    cocktail_glass_btn.setVisibility(View.GONE);
                    cancel_btn.setVisibility(View.GONE);
                    complete_btn.setVisibility(View.GONE);
                    result_.setVisibility(View.GONE);
                    if (anchorNode_u != null) {
                        anchorNode_u.setEnabled(false);
                    }
                    while (numberOfAnchors != 0) {
                        deleteButton.performClick();
                    }
                    numberOfAnchors = MAX_ANCHORS;
                    start_guide(cupname, cup_width, cup_height, width_pose, height_pose);
                }
            }
        });
        //취소 버튼 클릭리스너
        cancel_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                //여기서는 모든걸 원점으로 되돌림,,
                cancle = true;
                mug_cup_btn.setVisibility(View.VISIBLE);
                wine_glass_btn.setVisibility(View.VISIBLE);
                cocktail_glass_btn.setVisibility(View.VISIBLE);
                complete_btn.setVisibility(View.GONE);
                cancel_btn.setVisibility(View.GONE);
                renderable_ui_info.getView().findViewById(R.id.close_btn).performClick();
                renderable_ui_result.getView().findViewById(R.id.close_btn).performClick();
                result_.setText("측정 결과 표시창.");
                result_.setVisibility(View.GONE);
                create_mode = 0;
                deleteButton.setVisibility(View.GONE);
                heightButton.setVisibility(View.GONE);
                leftButton.setVisibility(View.GONE);
                rightButton.setVisibility(View.GONE);
                cup_height = 0;
                cup_width = 0;
                cup_width_under = 0;
                width_pose = null;
                height_pose = null;
                width_pose_under = null;
                cupname = "";
                while (numberOfAnchors != 0) {
                    deleteButton.performClick();
                }
                numberOfAnchors = MAX_ANCHORS;
            }
        });
        //생성된 노드를 삭제하는 버튼 클릭리스너 - 가장 최근에 만든 버튼부터 삭제
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Delete the Anchor if it exists
                Log.d(TAG, "Deleteing anchor");
                if (numberOfAnchors != 0) {
                    removeAnchorNode();
                } else {
                    Toast.makeText(getBaseContext(), "no nodes", Toast.LENGTH_SHORT).show();
                }

            }
        });
        //벽을 평면으로 감지하지 못한 경우에는 강제로 높이를 측정할 노드를 생성시킴 = 높이 앵커 생성하는 버튼 클릭리스너
        heightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //height mode
                if (numberOfAnchors != 0) {
                    //하나 이상 만들고, 2개 이하. 그니까 딱 하나만 만들었을때 새로운 노드를 만들어줌
                    if (numberOfAnchors == MAX_ANCHORS && selectedAnchorNode == null)
                        Toast.makeText(getBaseContext(), "You already create 2 nodes for measure width or height", Toast.LENGTH_SHORT).show();
                    else createAnchorNode_height();
                } else {
                    Toast.makeText(getBaseContext(), "You must create 1 node", Toast.LENGTH_SHORT).show();
                }
            }
        });
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //height mode
                if (numberOfAnchors != 0) {
                    //하나 이상 만들고, 2개 이하. 그니까 딱 하나만 만들었을때 새로운 노드를 만들어줌
                    if (numberOfAnchors == MAX_ANCHORS && selectedAnchorNode_left == null)
                        Toast.makeText(getBaseContext(), "You already create 2 nodes for measure width or height", Toast.LENGTH_SHORT).show();
                    else createAnchorNode_underwidth_left(height_pose[1]);
                } else {
                    Toast.makeText(getBaseContext(), "You must create 1 node", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //height mode
                if (numberOfAnchors != 0) {
                    //하나 이상 만들고, 2개 이하. 그니까 딱 하나만 만들었을때 새로운 노드를 만들어줌
                    if (numberOfAnchors == MAX_ANCHORS && selectedAnchorNode_right == null)
                        Toast.makeText(getBaseContext(), "You already create 2 nodes for measure width or height", Toast.LENGTH_SHORT).show();
                    else createAnchorNode_underwidth_right(height_pose[1]);
                } else {
                    Toast.makeText(getBaseContext(), "You must create 1 node", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //노드 삭제하는 함수
    private void removeAnchorNode() {
        //Remove an anchor node - 가장 뒤(최근)생성된 노드
        arFragment.getArSceneView().getScene().removeChild(anchorNodeList.get(numberOfAnchors - 1));
        anchorNodeList.get(numberOfAnchors - 1).getAnchor().detach();
        anchorNodeList.get(numberOfAnchors - 1).setParent(null);
        anchorNodeList.remove(numberOfAnchors - 1);
        numberOfAnchors--;
        selectedAnchorNode = null;
    }

    //탭 했을때 노드 생성하고 측정하는걸 연결해주는? 함수
    @Override
    public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
        if (numberOfAnchors < MAX_ANCHORS) {
            // Create the Anchor. hit값에 따라서 앵커 생성
            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());
            anchorNode.setRenderable(sphere); //아까 만든 구

            //노드가 너무 크면 의도치않은 오차 및 보기가 힘들어서 줄여버림
            anchorNode.setLocalScale(new Vector3(0.6f, 0.6f, 0.6f));

            anchorNodeList.add(anchorNode);
            numberOfAnchors++;
            if (numberOfAnchors == MAX_ANCHORS && width_pose == null && height_pose == null) {   // 두 번째 노드를 찍으면... 바로 너비 계산
                Calc_distance("w"); // 너비 계산 -> 세로 노드 찍기로 자동 넘어감
            }

        } else if (create_mode != 0) { // 2번 이상 터치(이미 노드가 2번 생성됨)시 무조건 계산, 초기값이 2번 이상 터치 상태이므로 create_mode==1일 때만 작동하도록
            Log.d(TAG, "MAX_ANCHORS exceeded");
            // ui 계속 생성되는걸 막기 위해서 처음에 비어있지 않으면 떼라고 했음
            if (resultanchor != null) {
                resultanchor.getAnchor().detach();
                resultanchor.setParent(null);
            }

            if (height_pose == null && width_pose[0] != null) {
                Calc_distance("h"); // 높이 계산
                Toast.makeText(this, "측정이 끝났습니다. 완료 버튼을 눌러주세요", Toast.LENGTH_SHORT).show();
            } else if (numberOfAnchors == MAX_ANCHORS && width_pose != null && height_pose != null && width_pose_under == null && cupname.equals("paper")) {
                Calc_distance("w_under");
            } else {
                Toast.makeText(this, "측정이 끝났습니다. 완료 버튼을 눌러주세요", Toast.LENGTH_SHORT).show();
            }

        }
    }

    //측정 결과를 저장해주는 함수
    private void save_result(double result, String mode) {
        switch (mode) {
            case "w":
                cup_width = result;
                result_.setText("너비 측정 결과 : " + cup_width);
                break;
            case "h":
                cup_height = result;
                result_.setText("세로 측정 결과 : " + cup_height);
                break;
            case "w_under":
                cup_width_under = result;
                result_.setText("너비 측정 결과 : " + cup_width_under);
                break;
            default:
                break;
        }
    }

    //계산하고, 위 함수에 값 넘겨서 멤버변수로 저장해주고, pose도 다 저장해주는 함수
    public void Calc_distance(String s) {
        float x = anchorNodeList.get(0).getWorldPosition().x - anchorNodeList.get(1).getWorldPosition().x;
        float y = anchorNodeList.get(0).getWorldPosition().y - anchorNodeList.get(1).getWorldPosition().y;
        float z = anchorNodeList.get(0).getWorldPosition().z - anchorNodeList.get(1).getWorldPosition().z;
        //유클리디안 거리 계산 및, 소수점 두자리까지 반영
        double result = Math.round(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)) * 100 * 100) / 100.0;

        float mid_x = (anchorNodeList.get(0).getWorldPosition().x + anchorNodeList.get(1).getWorldPosition().x) / 2;
        float mid_y = (anchorNodeList.get(0).getWorldPosition().y + anchorNodeList.get(1).getWorldPosition().y) / 2;
        float mid_z = (anchorNodeList.get(0).getWorldPosition().z + anchorNodeList.get(1).getWorldPosition().z) / 2;
        float[] mid = {mid_x, mid_y, mid_z};
        float[] mid_q = {0.0f, 0.0f, 0.0f, 0.0f};
        Pose midPosition = new Pose(mid, mid_q);

        switch (s) {
            case "h":
                float[] pos1 = {anchorNodeList.get(0).getWorldPosition().x, anchorNodeList.get(0).getWorldPosition().y, anchorNodeList.get(0).getWorldPosition().z};
                float[] pos2 = {anchorNodeList.get(1).getWorldPosition().x, anchorNodeList.get(1).getWorldPosition().y, anchorNodeList.get(1).getWorldPosition().z};
                Pose pose1 = new Pose(pos1, mid_q);
                Pose pose2 = new Pose(pos2, mid_q);
                height_pose = new Pose[]{pose1, pose2};
                break;
            case "w":
                //가로 모드는 중간값을 넘겨준다. 세로는 그럴 필요 없는듯?
                float[] pos3 = {anchorNodeList.get(0).getWorldPosition().x, anchorNodeList.get(0).getWorldPosition().y, anchorNodeList.get(0).getWorldPosition().z};
                float[] pos4 = {anchorNodeList.get(1).getWorldPosition().x, anchorNodeList.get(1).getWorldPosition().y, anchorNodeList.get(1).getWorldPosition().z};
                Pose pose3 = new Pose(pos3, mid_q);
                Pose pose4 = new Pose(pos4, mid_q);
                width_pose = new Pose[]{pose3, pose4, midPosition};
                break;
            case "w_under":
                //가로 모드는 중간값을 넘겨준다. 세로는 그럴 필요 없는듯?
                float[] pos5 = {anchorNodeList.get(0).getWorldPosition().x, anchorNodeList.get(0).getWorldPosition().y, anchorNodeList.get(0).getWorldPosition().z};
                float[] pos6 = {anchorNodeList.get(1).getWorldPosition().x, anchorNodeList.get(1).getWorldPosition().y, anchorNodeList.get(1).getWorldPosition().z};
                Pose pose5 = new Pose(pos5, mid_q);
                Pose pose6 = new Pose(pos6, mid_q);
                width_pose_under = new Pose[]{pose5, pose6, midPosition};
                break;
        }


        //2d ui를 렌더링함. 방법 : 그냥 두 점 사이 중간값을 측정해서 노드 추가하고 그 위치에 띄워준 것.
        renderable_ui_info.getView().findViewById(R.id.close_btn).performClick();
        Session session = arFragment.getArSceneView().getSession();
        Anchor anchor = session.createAnchor(midPosition);
        resultanchor = new AnchorNode(anchor);
        resultanchor.setParent(arFragment.getArSceneView().getScene());
        resultanchor.setEnabled(true);
        //결과 넣기
        TextView text = renderable_ui_result.getView().findViewById(R.id.result_text);
        text.setText((result) + "cm");
        //닫기 누르면 사라지도록
        renderable_ui_result.getView().findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                resultanchor.setEnabled(false);

                if (s.equals("w") && cancle != true) {    // 너비 계산 닫기 누른 후, 세로 노드 찍기
                    renderable_ui_info.getView().findViewById(R.id.close_btn).performClick();
                    removeAnchorNode(); // 노드 하나 삭제
                    if (!cupname.equals("mug")) {
                        removeAnchorNode(); // 노드 둘다 삭제하고 중간에 노드 찍음
                        AnchorNode anchorNode_ = new AnchorNode(anchor);
                        anchorNode_.setParent(arFragment.getArSceneView().getScene());
                        anchorNode_.setRenderable(sphere);
                        anchorNode_.setLocalScale(new Vector3(0.6f, 0.6f, 0.6f));
                        anchorNodeList.add(anchorNode_);
                        numberOfAnchors++;
                    }
                    createAnchorNode_height();
                    heightButton.setVisibility(View.VISIBLE);
                    //위에서 닫고 다시 만든것 (세로 측정 안내 텍스트용으로)
                    Frame frame = arFragment.getArSceneView().getArFrame();
                    Anchor anchor = session.createAnchor(
                            frame.getCamera().getPose()
                                    .compose(Pose.makeTranslation(0, 0, -1f)) //This will place the anchor 1M in front of the camera
                                    .extractTranslation());
                    AnchorNode addedAnchorNode = new AnchorNode(anchor);
                    addedAnchorNode.setEnabled(true);
                    TextView text = renderable_ui_info.getView().findViewById(R.id.result_text);
                    String msg =
                            "높이 측정 방법 : \n\n" +
                                    "상단의 세로 아이콘 버튼을 눌러 두 점 사이의 간격을 조정해 컵의 높이를 설정해주세요." +
                                    "\n\n설정 후에는 화면을 한번 더 터치하세요." +
                                    "\n\n높이 측정이 종료되면, 완료 버튼을 눌러주세요.\n";
                    text.setText(msg);
                    //닫기 누르면 사라지도록
                    renderable_ui_info.getView().findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addedAnchorNode.setEnabled(false);
                        }
                    });
                    addedAnchorNode.setLocalScale(new Vector3(0.4f, 0.4f, 0.4f));
                    addedAnchorNode.setRenderable(renderable_ui_info);
                    addedAnchorNode.setParent(arFragment.getArSceneView().getScene());
                }
                if (s.equals("h") && cancle != true && cupname.equals("paper")) {    // 너비 계산 닫기 누른 후, 세로 노드 찍기
                    removeAnchorNode();
                    removeAnchorNode();
                    Anchor anchor_under = session.createAnchor(height_pose[1]);
                    anchorNode_u = new AnchorNode(anchor_under);
                    anchorNode_u.setParent(arFragment.getArSceneView().getScene());

                    //중심점 찍고 그 주변, 양옆으로 한 1cm정도 떨어진 곳에 노드를 추가하고, 움직이게 할거임!중심점은 표시되겠고,
                    //그 중심점은 노드리스트에 들어가면 안됨!
                    heightButton.setVisibility(View.GONE);
                    leftButton.setVisibility(View.VISIBLE);
                    rightButton.setVisibility(View.VISIBLE);
                    createAnchorNode_underwidth_left(anchor_under.getPose());
                    createAnchorNode_underwidth_right(anchor_under.getPose());
                    //위에서 닫고 다시 만든것 (세로 측정 안내 텍스트용으로)
                    Frame frame = arFragment.getArSceneView().getArFrame();
                    Anchor anchor = session.createAnchor(
                            frame.getCamera().getPose()
                                    .compose(Pose.makeTranslation(0, 0, -1f)) //This will place the anchor 1M in front of the camera
                                    .extractTranslation());
                    AnchorNode addedAnchorNode_ = new AnchorNode(anchor);
                    addedAnchorNode_.setEnabled(true);
                    TextView text = renderable_ui_info.getView().findViewById(R.id.result_text);
                    String msg =
                            "밑면 너비 측정 방법 : \n\n" +
                                    "화면에 보이는 컵의 밑면 지름을 측정하도록 화면을 터치하여 두 점을 설정해주세요." +
                                    "\n\n측정이 완료되면, 높이 측정 단계로 넘어갑니다.";
                    text.setText(msg);
                    //닫기 누르면 사라지도록
                    renderable_ui_info.getView().findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addedAnchorNode_.setEnabled(false);
                        }
                    });
                    addedAnchorNode_.setLocalScale(new Vector3(0.4f, 0.4f, 0.4f));
                    addedAnchorNode_.setRenderable(renderable_ui_info);
                    addedAnchorNode_.setParent(arFragment.getArSceneView().getScene());
                }
            }
        });
        resultanchor.setRenderable(renderable_ui_result);
        resultanchor.setLocalScale(new Vector3(0.4f, 0.4f, 0.4f));

        save_result(result, s);
    }

    //높이 앵커 생성 함수
    public void createAnchorNode_height() {
        if (numberOfAnchors < MAX_ANCHORS) {
            //새로운 높이를 표현할 앵커 설치, 기준이 될 앵커의 pose를 가져옴 : 가장 처음에 만든 앵커의 포즈를 가져옴
            //그 포즈를 기준으로 y가 5cm 높은 pose를 생성 - 원기둥을 빼고는 다 아래로 가야함.
            Pose newpose = null;
            switch (cupname) {
                case "mug":
                    Pose standard = anchorNodeList.get(0).getAnchor().getPose();
                    newpose = standard.compose(Pose.makeTranslation(0, 0.05f, 0));
                    break;
                case "paper":
                case "cocktail":
                    Pose standard_else = width_pose[2];
                    newpose = standard_else.compose(Pose.makeTranslation(0, -0.05f, 0));
                    break;
            }
            Session session = arFragment.getArSceneView().getSession();
            Anchor anchor = session.createAnchor(newpose);
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());
            anchorNode.setRenderable(sphere);
            anchorNode.setLocalScale(new Vector3(0.6f, 0.6f, 0.6f));

            //이후 한번 더 누르면 이 앵커를 이동시켜야하기때문에 이 노드를 선택시킴
            selectedAnchorNode = anchorNode;
            anchorNodeList.add(anchorNode);
            numberOfAnchors++;
        } else {
            //노드가 2개 생성된 상태에서 한번 더 누르게 되면, 그 노드를 위로 이동시킴
            Anchor currentAnchor = selectedAnchorNode.getAnchor();
            //높이노드 생성시와 마찬가지로, 포즈를 가져와서 y를 이동시킨 pose를 만들어줌 - 원기둥만 위로 올라가야함
            Pose oldPose = currentAnchor.getPose();
            Pose newPose = null;
            switch (cupname) {
                case "mug":
                    newPose = oldPose.compose(Pose.makeTranslation(0, 0.01f, 0));
                    break;
                case "paper":
                case "cocktail":
                    newPose = oldPose.compose(Pose.makeTranslation(0, -0.01f, 0));
                    break;
            }
            selectedAnchorNode = moveRenderable(selectedAnchorNode, newPose, "h");
        }
    }

    //밑면 너비 왼쪽 앵커 생성 함수
    public void createAnchorNode_underwidth_right(Pose standardanchor) {
        if (numberOfAnchors < MAX_ANCHORS) {
            //중심점은 2번인덱스, 0과 1은 윗면!!
            //중심이랑 y ,z 같고 x만 다르게 할거임
            Pose newpose = standardanchor.compose(Pose.makeTranslation(0.05f, 0, 0));
            Session session = arFragment.getArSceneView().getSession();
            Anchor anchor = session.createAnchor(newpose);
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());
            anchorNode.setRenderable(under_sphere);
            anchorNode.setLocalScale(new Vector3(0.6f, 0.6f, 0.6f));
            anchorNodeList.add(1, anchorNode);
            numberOfAnchors++;
            //이후 한번 더 누르면 이 앵커를 이동시켜야하기때문에 이 노드를 선택시킴
            selectedAnchorNode_right = anchorNode;
        } else {
            Anchor currentAnchor = selectedAnchorNode_right.getAnchor();
            Pose oldPose = currentAnchor.getPose();
            Pose newPose = oldPose.compose(Pose.makeTranslation(0.01f, 0, 0));
            selectedAnchorNode_right = moveRenderable(selectedAnchorNode_right, newPose, "u_r");
        }
    }

    //밑면 너비 오른쪽 앵커 함수
    public void createAnchorNode_underwidth_left(Pose standardanchor) {
        if (numberOfAnchors < MAX_ANCHORS) {
            //중심점은 2번인덱스, 0과 1은 윗면!!
            //중심이랑 y ,z 같고 x만 다르게 할거임
            Pose newpose1 = standardanchor.compose(Pose.makeTranslation(-0.05f, 0, 0));
            Session session = arFragment.getArSceneView().getSession();
            Anchor anchor1 = session.createAnchor(newpose1);
            AnchorNode anchorNode1 = new AnchorNode(anchor1);
            anchorNode1.setParent(arFragment.getArSceneView().getScene());
            anchorNode1.setRenderable(under_sphere);
            anchorNode1.setLocalScale(new Vector3(0.6f, 0.6f, 0.6f));
            anchorNodeList.add(0, anchorNode1);
            numberOfAnchors++;
            //이후 한번 더 누르면 이 앵커를 이동시켜야하기때문에 이 노드를 선택시킴
            selectedAnchorNode_left = anchorNode1;

        } else {
            //노드가 2개 생성된 상태에서 한번 더 누르게 되면, 그 노드를 위로 이동시킴
            Anchor currentAnchor = selectedAnchorNode_left.getAnchor();
            //높이노드 생성시와 마찬가지로, 포즈를 가져와서 y를 이동시킨 pose를 만들어줌 - 원기둥만 위로 올라가야함
            Pose oldPose = currentAnchor.getPose();
            Pose newPose = oldPose.compose(Pose.makeTranslation(-0.01f, 0, 0));
            selectedAnchorNode_left = moveRenderable(selectedAnchorNode_left, newPose, "u_l");
        }
    }

    //높이 미세조절하도록 앵커 옮겨주는 함수
    private AnchorNode moveRenderable(AnchorNode anchorNodeToMove, Pose newPoseToMoveTo, String mode) {
        //지우고 다시 생성해서 리턴해줌
        arFragment.getArSceneView().getScene().removeChild(anchorNodeToMove);
        anchorNodeList.remove(anchorNodeToMove);
        Session session = arFragment.getArSceneView().getSession();
        Anchor anchor = session.createAnchor(newPoseToMoveTo.extractTranslation());
        AnchorNode newanchorNode = new AnchorNode(anchor);
        if (mode.equals("u_l") || mode.equals("u_r")) newanchorNode.setRenderable(under_sphere);
        else newanchorNode.setRenderable(sphere);
        newanchorNode.setParent(arFragment.getArSceneView().getScene());
        newanchorNode.setLocalScale(new Vector3(0.6f, 0.6f, 0.6f));
        if (mode.equals("u_l")) anchorNodeList.add(0, newanchorNode);
        else if (mode.equals("u_r")) anchorNodeList.add(1, newanchorNode);
        else anchorNodeList.add(newanchorNode);

        return newanchorNode;
    }

    //가이드 시작하는 함수
    private void start_guide(String cupname, double cup_width, double cup_height, Pose[] width_pose, Pose[] height_pose) {
        //원을 만든 후에는 특정 높이만큼을 올려서 앵커들 복사하고 복사한 애들끼리 연결해주면 되는데, 그렇다면 height pose는 쓸모가 없는가?
        //첫 포즈를 기준으로 y축 중심 10도 회전한 pose를 생성 => 그냥 앵커 고개를 회전시킴 안됌
        //원의 방정식을 이용 가로 배열 마지막 원소가 미드 포지션임. 중심 (a, b, c)라고 하면 y값은 무시하고 x와 z로만 방정식을 만듦
        //(x-a)^2 + (z-c)^2 = r^2 으로 이 위에 있는 점들을 구한다.
        //인자로 넘어온 radius 값은 cm단위고 오픈gl은 m단위체계이므로 100을 나눠주도록 한다.
        //<editor-fold desc="원 만들어주는 과정">
        double radius = (cup_width / 2) / 100;

        Pose midPosition = width_pose[2];

        //14각형, 8개[0-7] x 포지션, 양끝단 노드 2개 빼면 12개 포지션들 필요
        float[] x_positions = new float[8];
        x_positions[0] = width_pose[0].tx();
        x_positions[7] = width_pose[1].tx();

        float[] z_positions = calc_position(radius, midPosition, x_positions);

        Pose[] pose_list = make_pose(x_positions, width_pose[0], z_positions);

        pose_list[0] = width_pose[0];
        pose_list[13] = width_pose[1];

        Session session = arFragment.getArSceneView().getSession();
        Anchor[] anchors = new Anchor[14];
        AnchorNode[] anchornodes = new AnchorNode[14];

        //0과 마지막 13번인덱스는 위에서 줬음.
        for (int i = 1; i < 13; i++) {
            anchors[i] = session.createAnchor(pose_list[i]);
            anchornodes[i] = new AnchorNode(anchors[i]);
            place(anchornodes[i], "");
        }
        //0과 마지막 13번째는 빨간색 노드로 표시할거임
        anchors[0] = session.createAnchor(pose_list[0]);
        anchornodes[0] = new AnchorNode(anchors[0]);
        place(anchornodes[0], "r");
        anchors[13] = session.createAnchor(pose_list[13]);
        anchornodes[13] = new AnchorNode(anchors[13]);
        place(anchornodes[13], "r");
        //</editor-fold desc="원 만들어주는 과정">
        float height;
        switch (cupname) {
            case "mug":
                //가이드 진행에 맞게 height 맞춰서 make실린더도 변수를 바꿔가며 실행시키면 된다.
                //선택된 칵테일 여기서 불러와서 쓰면 됨. selected_cocktail

                height = 0.03f;
                make_cylinder(radius, height, midPosition, width_pose[0]);
                break;
            case "cocktail":
                //일단 원을 서로 잇고, 거기를 불투명한 텍스쳐로 채워주는것 부터 한다.
                //AnchorNode[] anchornodes = new AnchorNode[8]; 이부분이 8각형 찍은 부분, 0과 7번째 인덱스가 양 끝단
                //아래 함수는 position 중심으로하고 nodes들을 2씩 묶어서 만들거임
                make_polygon(anchornodes, null, midPosition, "circle");
                make_polygon(anchornodes, null, height_pose[1], "side");
                //얘는 높이에 따라서 원들의 y값을 바꿔줘야 할듯..?
                break;
            case "paper":
                //아오 함수로 만들걸
                //<editor-fold desc="밑면 원 만들어주는 과정">
                double radius_under = (cup_width_under / 2) / 100;

                Pose midPosition_under = width_pose_under[2];

                //14각형, 8개[0-7] x 포지션, 양끝단 노드 2개 빼면 12개 포지션들 필요
                float[] x_positions_under = new float[8];
                x_positions_under[0] = width_pose_under[0].tx();
                x_positions_under[7] = width_pose_under[1].tx();

                float[] z_positions_under = calc_position(radius_under, midPosition_under, x_positions_under);

                Pose[] pose_list_under = make_pose(x_positions_under, width_pose_under[0], z_positions_under);

                pose_list_under[0] = width_pose_under[0];
                pose_list_under[13] = width_pose_under[1];
                Anchor[] anchors_under = new Anchor[14];
                AnchorNode[] anchornodes_under = new AnchorNode[14];

                //0과 마지막 13번인덱스는 위에서 줬음.
                for (int i = 1; i < 13; i++) {
                    anchors_under[i] = session.createAnchor(pose_list_under[i]);
                    anchornodes_under[i] = new AnchorNode(anchors_under[i]);
                    place(anchornodes_under[i], "");
                }
                //0과 마지막 13번째는 빨간색 노드로 표시할거임
                anchors_under[0] = session.createAnchor(pose_list_under[0]);
                anchornodes_under[0] = new AnchorNode(anchors_under[0]);
                place(anchornodes_under[0], "r");
                anchors_under[13] = session.createAnchor(pose_list_under[13]);
                anchornodes_under[13] = new AnchorNode(anchors_under[13]);
                place(anchornodes_under[13], "r");
                //</editor-fold desc="밑면 원 만들어주는 과정">
                make_polygon(anchornodes, null, midPosition, "circle");
                make_polygon(anchornodes_under, null, midPosition_under, "circle");
                AnchorNode[][] anchorNodesArray = {anchornodes, anchornodes_under};
                make_polygon(null, anchorNodesArray, midPosition_under, "square");
            default:
                break;
        }
    }

    //가이드 밑면 원호 그리는 포지션들 계산해서 z리스트 리턴
    private float[] calc_position(double radius, Pose midposition, float[] x) {
        float[] z_list = new float[14]; // 0과 11번째 인덱스는 비워둘거임(걍 헷갈려서 크기 맞춤,,)
        float cm = ((x[7] - x[0]) / 7);
        for (int i = 1; i < 7; i++) {
            x[i] = x[0] + i * cm;
            z_list[(2 * i) - 1] = (float) Math.sqrt(Math.pow(radius, 2) - Math.pow((x[i] - midposition.tx()), 2)) + midposition.tz();
            z_list[2 * i] = (float) -Math.sqrt(Math.pow(radius, 2) - Math.pow((x[i] - midposition.tx()), 2)) + midposition.tz();
        }
        return z_list;
    }

    //가이드 밑면 원 그려줄 앵커들 포즈 생성하는 함수
    private Pose[] make_pose(float[] x, Pose w_pos, float[] z) {
        Pose[] p_list = new Pose[14];
        float[][] position = new float[12][];
        float[] quat = {0.0f, 0.0f, 0.f, 0.0f};
        int i = 1;
        for (int j = 0; j < 12; j++) {
            position[j] = new float[]{x[i], w_pos.ty(), z[j + 1]};
            if ((j + 1) % 2 == 0) i++;
        }
        for (int j = 1; j < 13; j++) {
            p_list[j] = new Pose(position[j - 1], quat);
        }
        return p_list;
    }

    //가이드 밑면 원 그려줄 앵커들 화면에 배치해주는 함수
    private void place(AnchorNode anchorNode, String s) {
        Color color1 = new Color(255, 0, 0, 0.6f);
        Color color2 = new Color(255, 102, 102, 0.6f);
        MaterialFactory.makeTransparentWithColor(this, color1)
                .thenAccept(material -> {
                    Renderable sphere;
                    sphere = ShapeFactory.makeSphere(0.015f, Vector3.zero(), material);
                    sphere.setShadowCaster(false);
                });
        MaterialFactory.makeTransparentWithColor(this, color2)
                .thenAccept(material -> {
                    Renderable guide_sphere;
                    guide_sphere = ShapeFactory.makeSphere(0.015f, Vector3.zero(), material);
                    guide_sphere.setShadowCaster(false);
                });


        anchorNode.setParent(arFragment.getArSceneView().getScene());
        anchorNode.setRenderable(guide_sphere);
        anchorNode.setLocalScale(new Vector3(0.2f, 0.2f, 0.2f));
        if (Objects.equals(s, "r")) {
            anchorNode.setParent(arFragment.getArSceneView().getScene());
            anchorNode.setRenderable(sphere);
            anchorNode.setLocalScale(new Vector3(0.2f, 0.2f, 0.2f));
        }
    }

    //부피 구해주는 함수
    private double calculateVolume(double radius, double height) {
        return Math.pow(radius, 2) * Math.PI * height;
    }

    //계량 높이 구해주는 함수 - 원기둥
    private double calculateHeight_cylinder(double amount, double radius) {
        return amount / Math.pow(radius, 2) * Math.PI;
    }

    //계량 높이 구해주는 함수 - 원뿔
    private double calculateHeight_cone(double amount, double radius, double height) {
        return Math.pow((3 * Math.pow(height, 2) * amount / (Math.PI * Math.pow(radius, 2))), 1.0 / 3.0);
    }

    //계량 높이 구해주는 함수 - 원뿔대
    private double calculateHeight_truncated(double amount, double bottom_radius, double top_radius, double height) {
        double a = (Math.pow(top_radius, 2) * Math.PI) / (3 * Math.pow(height, 2));
        double b = (top_radius * bottom_radius * Math.PI) / (3 * height);
        double c = (Math.pow(bottom_radius, 2) * Math.PI) / 3;
        double d = -1 * amount;
        double e = 2 * Math.pow(b, 3) - 9 * a * b * c + 27 * Math.pow(a, 2) * d;
        double f = Math.pow(Math.pow(e, 2) - 4 * Math.pow(Math.pow(b, 2) - 3 * a * c, 3), 1.0 / 2.0);
        return -1 * (b / (3 * a)) - (1 / (3 * a)) * Math.pow((e + f) / 2, 1.0 / 3.0)
                - (1 / (3 * a)) * Math.pow((e - f) / 2, 1.0 / 3.0);
    }

    //원기둥 그려주는 함수.
    private void make_cylinder(double radius, float height, Pose mid, Pose width) {
        Color color2 = new Color(255, 140, 50, 0.35f);
        MaterialFactory.makeTransparentWithColor(this, color2)
                .thenAccept(material -> {
                    Renderable Cylinder;
                    //컵의 두께와 노드를 생각한다면 radius를 좀 더 빼줘도 됨.
                    //랜더러는 앵커(노드)를 중심으로해서 위로 반, 아래로 반 생성된다는 점 잊지말기
                    Cylinder = ShapeFactory.makeCylinder((float) radius - 0.00025f, height, new Vector3(0.0f, height / 2, 0.0f), material);
                    Cylinder.setShadowCaster(false);
                    float[] position = {mid.tx(), width.ty(), mid.tz()};
                    float[] q = {0.0f, 0.0f, 0.0f, 0.0f};
                    Pose midpose = new Pose(position, q);
                    Session session = arFragment.getArSceneView().getSession();
                    Anchor anchor = session.createAnchor(midpose);
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());
                    anchorNode.setRenderable(Cylinder);
                });
    }

    //원을 그려줄 함수
    private void make_polygon(AnchorNode[] anchorNodes, AnchorNode[][] anchorNodes_, Pose pos, String polygon) {
        Session session = arFragment.getArSceneView().getSession();
        if (polygon.equals("circle") || polygon.equals("side")) {
            Anchor anchor_pos = session.createAnchor(pos);
            AnchorNode anchornode_pos = new AnchorNode(anchor_pos);
            anchornode_pos.setParent(arFragment.getArSceneView().getScene());
            //인덱스가 이상해서 .. ㅜ
            for (int i = 0; i < 3; i++) {
                if (i / 2 == 1) {
                    List<AnchorNode> anchorsList = new ArrayList<>();
                    anchorsList.add(anchorNodes[12]);
                    anchorsList.add(anchorNodes[13]);
                    anchorsList.add(anchornode_pos);
                    make_mash(anchorsList);
                } else {
                    List<AnchorNode> anchorsList = new ArrayList<>();
                    anchorsList.add(anchorNodes[0]);
                    anchorsList.add(anchorNodes[i + 1]);
                    anchorsList.add(anchornode_pos);
                    make_mash(anchorsList);
                }
            }
            for (int i = 1; i < 12; i++) {
                List<AnchorNode> anchorsList = new ArrayList<>();
                anchorsList.add(anchorNodes[i]);
                anchorsList.add(anchorNodes[i + 2]);
                anchorsList.add(anchornode_pos);
                make_mash(anchorsList);
            }
        } else if (polygon.equals("square")) {
            //AnchorNode[][] anchorNodes_
            for (int i = 0; i < 2; i++) {
                List<AnchorNode> anchorsList = new ArrayList<>();
                anchorsList.add(anchorNodes_[0][i]);
                anchorsList.add(anchorNodes_[0][(2 * i) + 1]);
                anchorsList.add(anchorNodes_[1][i]);
                make_mash(anchorsList);
                anchorsList = new ArrayList<>();
                anchorsList.add(anchorNodes_[0][i]);
                anchorsList.add(anchorNodes_[1][Math.abs((2 * i) - 2)]);
                anchorsList.add(anchorNodes_[1][i]);
                make_mash(anchorsList);
            }
            for (int i = 1; i < 6; i++) {
                List<AnchorNode> anchorsList = new ArrayList<>();
                anchorsList.add(anchorNodes_[0][(2 * i) + 1]);
                anchorsList.add(anchorNodes_[0][(2 * i) + 3]);
                anchorsList.add(anchorNodes_[1][(2 * i) + 1]);
                make_mash(anchorsList);
                anchorsList = new ArrayList<>();
                anchorsList.add(anchorNodes_[0][(2 * i) + 1]);
                anchorsList.add(anchorNodes_[1][(2 * i) - 1]);
                anchorsList.add(anchorNodes_[1][(2 * i) + 1]);
                make_mash(anchorsList);
            }
            List<AnchorNode> anchorsList = new ArrayList<>();
            anchorsList.add(anchorNodes_[0][13]);
            anchorsList.add(anchorNodes_[0][12]);
            anchorsList.add(anchorNodes_[1][13]);
            make_mash(anchorsList);
            anchorsList = new ArrayList<>();
            anchorsList.add(anchorNodes_[0][13]);
            anchorsList.add(anchorNodes_[1][11]);
            anchorsList.add(anchorNodes_[1][13]);
            make_mash(anchorsList);
            anchorsList = new ArrayList<>();
            anchorsList.add(anchorNodes_[0][12]);
            anchorsList.add(anchorNodes_[0][10]);
            anchorsList.add(anchorNodes_[1][12]);
            make_mash(anchorsList);
            anchorsList = new ArrayList<>();
            anchorsList.add(anchorNodes_[0][12]);
            anchorsList.add(anchorNodes_[1][13]);
            anchorsList.add(anchorNodes_[1][12]);
            make_mash(anchorsList);


            for (int i = 10; i > 1; i = i - 2) {
                anchorsList = new ArrayList<>();
                anchorsList.add(anchorNodes_[0][i]);
                anchorsList.add(anchorNodes_[0][Math.abs(2 - i)]);
                anchorsList.add(anchorNodes_[1][i]);
                make_mash(anchorsList);
                anchorsList = new ArrayList<>();
                anchorsList.add(anchorNodes_[0][i]);
                anchorsList.add(anchorNodes_[1][2 + i]);
                anchorsList.add(anchorNodes_[1][i]);
                make_mash(anchorsList);
            }
        }


    }

    //매쉬 만들어주는 함수
    private void make_mash(List<AnchorNode> anchorsList) {
        if (anchorsList.size() == 3) {
            Color color2 = new Color(255, 0, 0, 0.5f);
            MaterialFactory.makeTransparentWithColor(this, color2)
                    .thenAccept(material -> {
                        final Node node = new Node();
                        final ModelRenderable triangle = makeTriangleWithAnchors(anchorsList, material);
                        node.setParent(arFragment.getArSceneView().getScene());
                        node.setRenderable(triangle);
                    });
        }
    }

    private ModelRenderable makeTriangleWithAnchors(@NonNull final List<AnchorNode> anchorNodes, @NonNull final Material material) {
        if (anchorNodes.size() != 3)
            throw new IllegalStateException("Different count of anchorsList than 3");

        final Vector3 p0 = anchorNodes.get(0).getLocalPosition();
        final Vector3 p1 = anchorNodes.get(1).getLocalPosition();
        final Vector3 p2 = anchorNodes.get(2).getLocalPosition();
        final Vector3 up = Vector3.up();
        final Vertex.UvCoordinate uvTop = new Vertex.UvCoordinate(0.5f, 1.0f);
        final Vertex.UvCoordinate uvBotLeft = new Vertex.UvCoordinate(0.0f, 0.0f);
        final Vertex.UvCoordinate uvBotRight = new Vertex.UvCoordinate(1.0f, 0.0f);
        final List<Vertex> vertices = new ArrayList<>(Arrays.asList(
                Vertex.builder().setPosition(p0).setNormal(up).setUvCoordinate(uvTop).build(),
                Vertex.builder().setPosition(p1).setNormal(up).setUvCoordinate(uvBotRight).build(),
                Vertex.builder().setPosition(p2).setNormal(up).setUvCoordinate(uvBotLeft).build()
        ));

        final List<Integer> triangleIndices = new ArrayList<>(3);
        triangleIndices.add(0);
        triangleIndices.add(2);
        triangleIndices.add(1);
        triangleIndices.add(0);
        triangleIndices.add(1);
        triangleIndices.add(2);

        final RenderableDefinition.Submesh submesh = RenderableDefinition.Submesh.builder()
                .setTriangleIndices(triangleIndices)
                .setMaterial(material)
                .build();
        final RenderableDefinition renderableDefinition = RenderableDefinition.builder()
                .setVertices(vertices)
                .setSubmeshes(Arrays.asList(submesh))
                .build();
        final CompletableFuture future = ModelRenderable.builder()
                .setSource(renderableDefinition)
                .build();

        final ModelRenderable result;
        try {
            result = (ModelRenderable) future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new AssertionError("Error creating renderable.", e);
        }

        if (result == null) {
            throw new AssertionError("Error creating renderable.");
        } else {
            return result;
        }
    }
}
