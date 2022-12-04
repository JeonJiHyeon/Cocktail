package com.navi_baekgu.ui.recipe;

import static com.google.ar.sceneform.rendering.HeadlessEngineWrapper.TAG;

import java.lang.Math;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.RenderableInstance;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.navi_baekgu.MainActivity;
import com.navi_baekgu.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.google.ar.sceneform.ux.ArFragment;

import cn.pedant.SweetAlert.SweetAlertDialog;


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
    private double cup_width;
    private Pose width_pose[];
    private Pose height_pose[];
    //지금 선택한 노드를 가르킬것 - height 측정시 사용됨
    private AnchorNode selectedAnchorNode = null;
    //버튼 생성
    private FloatingActionButton deleteButton;
    private FloatingActionButton heightButton;
    private Button mug_cup_btn;
    private Button wine_glass_btn;
    private Button cocktail_glass_btn;
    private Button cancel_btn;
    private Button complete_btn;
    private Button result_;
    private String cupname;

    private ArFragment arFragment;
    //컬러와 렌더러블<3D model and consists of vertices, materials, textures, and more.> 생성
    private final Color color = new Color(android.graphics.Color.parseColor("#ff0051"));
    private Renderable sphere;
    private ViewRenderable renderable_ui_info, renderable_ui_result;
    private AnchorNode resultanchor;
    //</editor-fold desc="변수 생성구간">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cameraguide);
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
        result_ = (Button)findViewById(R.id.flag);
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
                    sphere.setShadowReceiver(false);
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
                cupname = "mug";
                mug_cup_btn.setVisibility(View.GONE);
                cancel_btn.setVisibility(View.VISIBLE);
                numberOfAnchors = 0;
                create_mode = 1;
                deleteButton.setVisibility(View.VISIBLE);
                heightButton.setVisibility(View.VISIBLE);
                wine_glass_btn.setVisibility(View.GONE);
                cocktail_glass_btn.setVisibility(View.GONE);
                result_.setVisibility(View.VISIBLE);
                complete_btn.setVisibility(View.VISIBLE);

                //2d ui로 설명하기
                try{Frame frame = arFragment.getArSceneView().getArFrame();
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
                            "화면에 보이는 컵의 밑면 지름을 측정하도록 두 점을 설정해주세요." +
                            "\n\n측정이 완료되면, 높이 측정 단계로 넘어갑니다."+
                            "\n\n높이 측정 방법 : \n\n" +
                            "상단의 세로 아이콘 버튼을 눌러 두 점 사이의 간격을 조정해 컵의 높이를 설정해주세요."+
                            "\n\n높이 측정이 종료되면, 완료 버튼을 눌러주세요.\n";
                    text.setText(msg);
                    //닫기 누르면 사라지도록
                    renderable_ui_info.getView().findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getBaseContext(), "Clicked_close_btn", Toast.LENGTH_SHORT).show();
                            addedAnchorNode.setEnabled(false);
                        }
                    });
                    addedAnchorNode.setLocalScale(new Vector3(0.4f, 0.4f, 0.4f));
                    addedAnchorNode.setRenderable(renderable_ui_info);
                    addedAnchorNode.setParent(arFragment.getArSceneView().getScene());
                }
                catch (Exception e){
                    new SweetAlertDialog(CameraguideActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("카메라는 컵을 향하게 들어주세요!")
                            .show();
                    mug_cup_btn.setVisibility(View.VISIBLE);
                    cancel_btn.setVisibility(View.GONE);
                    result_.setVisibility(View.GONE);
                    complete_btn.setVisibility(View.GONE);
                    numberOfAnchors = MAX_ANCHORS;
                    create_mode = 0;
                    deleteButton.setVisibility(View.GONE);
                    heightButton.setVisibility(View.GONE);
                }
            }
        });
        //완료 버튼 -가이드 시작 버튼- 클릭리스너
        complete_btn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(cup_width<=0&&cup_height<=0){
                    Toast.makeText(CameraguideActivity.this, "길이 측정 완료 후 눌러주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(cup_height<=0){
                    Toast.makeText(CameraguideActivity.this, "높이 측정이 완료되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    //이 함수 아직 안만듬!
                    //start_guide(cupname, cup_width, cup_height, width_pose, height_pose);
                }
            }
        });
        //취소 버튼 클릭리스너
        cancel_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                //여기서는 모든걸 원점으로 되돌림,,
                mug_cup_btn.setVisibility(View.VISIBLE);
                wine_glass_btn.setVisibility(View.VISIBLE);
                cocktail_glass_btn.setVisibility(View.VISIBLE);
                cancel_btn.setVisibility(View.GONE);
                renderable_ui_info.getView().findViewById(R.id.close_btn).performClick();
                result_.setText("측정 결과 표시창.");
                result_.setVisibility(View.GONE);
                create_mode = 0;
                deleteButton.setVisibility(View.GONE);
                heightButton.setVisibility(View.GONE);
                cup_height = 0;
                cup_width = 0;
                width_pose = null;
                height_pose = null;
                cupname = "";
                while (numberOfAnchors!=0) {
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
            if (numberOfAnchors == MAX_ANCHORS && width_pose==null&&height_pose==null) {   // 두 번째 노드를 찍으면... 바로 너비 계산
                Toast.makeText(this, "Calc distance for width", Toast.LENGTH_SHORT).show();
                Calc_distance("w"); // 너비 계산 -> 세로 노드 찍기로 자동 넘어감
            }

        } else if(create_mode != 0) { // 2번 이상 터치(이미 노드가 2번 생성됨)시 무조건 계산, 초기값이 2번 이상 터치 상태이므로 create_mode==1일 때만 작동하도록
            Log.d(TAG,"MAX_ANCHORS exceeded");
            // ui 계속 생성되는걸 막기 위해서 처음에 비어있지 않으면 떼라고 했음
            if(resultanchor != null){
                resultanchor.getAnchor().detach();
                resultanchor.setParent(null);
            }

            if(height_pose==null&&width_pose[0]!=null){
                Toast.makeText(this, "Calc distance for height", Toast.LENGTH_SHORT).show();
                Calc_distance("h"); // 높이 계산
                Toast.makeText(this, "측정이 끝났습니다. 완료 버튼을 눌러주세요", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "측정이 끝났습니다. 완료 버튼을 눌러주세요", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //측정 결과를 저장해주는 함수
    private void save_result(double result, String mode){
        switch (mode){
            case "w":
                cup_width = result * 100;
                result_.setText("가로 측정 결과 : "+cup_width);
                break;
            case "h":
                cup_height = result * 100;
                result_.setText("세로 측정 결과 : "+cup_height);
                break;
            default:
                break;
        }
    }
    //계산하고, 위 함수에 값 넘겨서 멤버변수로 저장해주고, pose도 다 저장해주는 함수
    public void Calc_distance(String s){
        float x = anchorNodeList.get(0).getWorldPosition().x - anchorNodeList.get(1).getWorldPosition().x;
        float y = anchorNodeList.get(0).getWorldPosition().y - anchorNodeList.get(1).getWorldPosition().y;
        float z = anchorNodeList.get(0).getWorldPosition().z - anchorNodeList.get(1).getWorldPosition().z;
        //유클리디안 거리 계산 및, 소수점 두자리까지 반영
        double result = Math.round(Math.sqrt(Math.pow(x,2) + Math.pow(y,2) + Math.pow(z,2)) * 100) / 100.0;

        float mid_x = (anchorNodeList.get(0).getWorldPosition().x + anchorNodeList.get(1).getWorldPosition().x) /2;
        float mid_y = (anchorNodeList.get(0).getWorldPosition().y + anchorNodeList.get(1).getWorldPosition().y) /2;
        float mid_z = (anchorNodeList.get(0).getWorldPosition().z + anchorNodeList.get(1).getWorldPosition().z) /2;
        float[] mid = {mid_x,mid_y,mid_z};
        float[] mid_q = {0.0f,0.0f,0.0f,0.0f};
        Pose midPosition = new Pose(mid,mid_q);

        switch (s){
            case "h":
                float[] pos1 = {anchorNodeList.get(0).getWorldPosition().x,anchorNodeList.get(0).getWorldPosition().y,anchorNodeList.get(0).getWorldPosition().z};
                float[] pos2 = {anchorNodeList.get(1).getWorldPosition().x,anchorNodeList.get(1).getWorldPosition().y,anchorNodeList.get(1).getWorldPosition().z};
                Pose pose1 = new Pose(pos1,mid_q);
                Pose pose2 = new Pose(pos2,mid_q);
                height_pose = new Pose[]{pose1, pose2};
                break;
            case "w":
                float[] pos3 = {anchorNodeList.get(0).getWorldPosition().x,anchorNodeList.get(0).getWorldPosition().y,anchorNodeList.get(0).getWorldPosition().z};
                float[] pos4 = {anchorNodeList.get(1).getWorldPosition().x,anchorNodeList.get(1).getWorldPosition().y,anchorNodeList.get(1).getWorldPosition().z};
                Pose pose3 = new Pose(pos3,mid_q);
                Pose pose4 = new Pose(pos4,mid_q);
                width_pose = new Pose[]{pose3, pose4};
                break;
        }


        //2d ui를 렌더링함. 방법 : 그냥 두 점 사이 중간값을 측정해서 노드 추가하고 그 위치에 띄워준 것.
        Session session = arFragment.getArSceneView().getSession();
        Anchor anchor = session.createAnchor(midPosition);
        resultanchor = new AnchorNode(anchor);
        resultanchor.setParent(arFragment.getArSceneView().getScene());
        resultanchor.setEnabled(true);
        //결과 넣기
        TextView text = renderable_ui_result.getView().findViewById(R.id.result_text);
        text.setText( (result * 100)+"cm");
        //닫기 누르면 사라지도록
        renderable_ui_result.getView().findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), "Clicked_close_btn", Toast.LENGTH_SHORT).show();
                resultanchor.setEnabled(false);
                if (s.equals("w")) {    // 너비 계산 닫기 누른 후, 세로 노드 찍기
                    removeAnchorNode(); // 노드 하나 삭제
                    createAnchorNode_height();
                    Toast.makeText(getBaseContext(), "상단의 세로 아이콘 버튼을 눌러 컵의 높이를 설정해주세요. 설정이 끝나면 화면을 한 번 터치하세요.", Toast.LENGTH_LONG).show();
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
            Pose standard = anchorNodeList.get(0).getAnchor().getPose();
            //그 포즈를 기준으로 y가 5cm 높은 pose를 생성
            Pose newpose = standard.compose(Pose.makeTranslation(0, 0.05f, 0));

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
            //높이노드 생성시와 마찬가지로, 포즈를 가져와서 y를 이동시킨 pose를 만들어줌
            Pose oldPose = currentAnchor.getPose();
            Pose newPose = oldPose.compose(Pose.makeTranslation(0, 0.01f, 0));
            selectedAnchorNode = moveRenderable(selectedAnchorNode, newPose);
        }
    }
    //높이 미세조절하도록 앵커 옮겨주는 함수
    private AnchorNode moveRenderable(AnchorNode anchorNodeToMove, Pose newPoseToMoveTo) {
        //지우고 다시 생성해서 리턴해줌
        arFragment.getArSceneView().getScene().removeChild(anchorNodeToMove);
        anchorNodeList.remove(anchorNodeToMove);
        Session session = arFragment.getArSceneView().getSession();
        Anchor anchor = session.createAnchor(newPoseToMoveTo.extractTranslation());
        AnchorNode newanchorNode = new AnchorNode(anchor);
        newanchorNode.setRenderable(sphere);
        newanchorNode.setParent(arFragment.getArSceneView().getScene());
        newanchorNode.setLocalScale(new Vector3(0.6f, 0.6f, 0.6f));
        anchorNodeList.add(newanchorNode);

        return newanchorNode;
    }

}
