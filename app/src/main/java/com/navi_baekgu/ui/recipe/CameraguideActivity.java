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

public class CameraguideActivity extends AppCompatActivity  implements
        BaseArFragment.OnTapArPlaneListener {
    //최대 앵커 수를 2개로 제한함
    private  static final int MAX_ANCHORS = 2;
    //앵커노드리스트를 생성
    private List<AnchorNode> anchorNodeList = new ArrayList<>();
    //지금 몇개의 앵커가 있는지 저장할 변수
    private Integer numberOfAnchors = 0;


    //지금 선택한 노드를 가르킬것 - height 측정시 사용됨
    private AnchorNode selectedAnchorNode = null;

    //버튼 생성
    private FloatingActionButton deleteButton;
    private FloatingActionButton widthButton;
    private FloatingActionButton heightButton;
    private ArFragment arFragment;
    //컬러와 렌더러블<3D model and consists of vertices, materials, textures, and more.> 생성
    private final Color color = new Color(android.graphics.Color.parseColor("#ff0051"));
    private Renderable sphere;
    private ViewRenderable renderable_ui;

    private AnchorNode resultanchor;




//        히트말고 버튼이벤트로 노드 생성하기
//    public void planetrack(Renderable model, FloatingActionButton button) {
//        Frame frame = arFragment.getArSceneView().getArFrame();
//        for (Plane plane : frame.getUpdatedTrackables(Plane.class)) {
//            if (plane.getTrackingState() == TrackingState.TRACKING) {
//                setListener(model, button, plane.getCenterPose());
//            }
//        }
//    }
//    public void setListener(Renderable model, FloatingActionButton button, Pose pose){
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Frame frame = arFragment.getArSceneView().getArFrame();
//                Session session = arFragment.getArSceneView().getSession();
//                String positions = pose.toString();
//                Anchor anchor = session.createAnchor(pose);
//                AnchorNode anchorNode = new AnchorNode(anchor);
//                anchorNode.setParent(arFragment.getArSceneView().getScene());
//                TransformableNode modelNode = new TransformableNode(arFragment.getTransformationSystem());
//                modelNode.setParent(anchorNode);
//                RenderableInstance modelInstance = modelNode.setRenderable(model);
//                modelInstance.getMaterial().setInt("baseColorIndex", 0);
//                modelInstance.getMaterial().setTexture("baseColorMap", texture);
//
//                modelNode.select();
//
//                Toast.makeText(getApplicationContext(),positions, Toast.LENGTH_LONG).show();
//            }
//        });
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cameraguide);
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
                    Toast.makeText(this, "try to load model", Toast.LENGTH_SHORT).show();

                    CameraguideActivity activity = weakActivity.get();
                    if (activity != null) {
                        Toast.makeText(this, "not null", Toast.LENGTH_SHORT).show();
                        activity.renderable_ui = renderable;
                        renderable_ui.setShadowReceiver(false);
                        renderable_ui.setShadowCaster(false);
                    }
                }).exceptionally(throwable -> {
                    Toast.makeText(this, "Unable to load model", Toast.LENGTH_LONG).show();
                    return null;
                });

        //생성된 노드를 삭제하는 버튼 - 가장 최근에 만든 버튼부터 삭제
        deleteButton = findViewById(R.id.delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Delete the Anchor if it exists
                Log.d(TAG,"Deleteing anchor");
                if(numberOfAnchors != 0){
                    removeAnchorNode();
                }
                else {
                    Toast.makeText(getBaseContext(), "no nodes", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //벽을 평면으로 감지하지 못한 경우에는 강제로 높이를 측정할 노드를 생성시킴
        heightButton = findViewById(R.id.height);

        heightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //height mode
                if (numberOfAnchors != 0) {
                    //하나 이상 만들고, 2개 이하. 그니까 딱 하나만 만들었을때 새로운 노드를 만들어줌
                    if (numberOfAnchors==MAX_ANCHORS && selectedAnchorNode==null)
                        Toast.makeText(getBaseContext(), "You already create 2 nodes for measure width or height", Toast.LENGTH_SHORT).show();
                    else createAnchorNode_height();
                    }

                else{
                    Toast.makeText(getBaseContext(), "You must create 1 node", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void removeAnchorNode() {
        //Remove an anchor node - 가장 뒤(최근)생성된 노드
        arFragment.getArSceneView().getScene().removeChild(anchorNodeList.get(numberOfAnchors-1));
        anchorNodeList.get(numberOfAnchors-1).getAnchor().detach();
        anchorNodeList.get(numberOfAnchors-1).setParent(null);
        anchorNodeList.remove(numberOfAnchors-1);
        numberOfAnchors--;
        selectedAnchorNode = null;
    }

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

        } else { //2번 이상 터치(이미 노드가 2번 생성됨)시 무조건 계산
            Log.d(TAG,"MAX_ANCHORS exceeded");
            Toast.makeText(this, "Calc distance", Toast.LENGTH_SHORT).show();
            //계속 생성되는걸 막기 위해서 처음에 비어있지 않으면 떼라고 했음
            if(resultanchor != null){
                resultanchor.getAnchor().detach();
                resultanchor.setParent(null);
                Calc_distance();
            }
            else{
                Calc_distance();
            }

        }
    }

    public void Calc_distance(){
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

        //2d ui를 렌더링함. 방법 : 그냥 두 점 사이 중간값을 측정해서 노드 추가하고 그 위치에 띄워준 것.
        Session session = arFragment.getArSceneView().getSession();
        Anchor anchor = session.createAnchor(midPosition);
        resultanchor = new AnchorNode(anchor);
        resultanchor.setParent(arFragment.getArSceneView().getScene());
        resultanchor.setEnabled(true);
        //결과 넣기
        TextView text = renderable_ui.getView().findViewById(R.id.result_text);
        text.setText( (result * 100)+"cm");
        //닫기 누르면 사라지도록
        renderable_ui.getView().findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), "Clicked_close_btn", Toast.LENGTH_SHORT).show();
                resultanchor.setEnabled(false);
            }
        });
        resultanchor.setRenderable(renderable_ui);
        resultanchor.setLocalScale(new Vector3(0.4f, 0.4f, 0.4f));

    }

    public void createAnchorNode_height(){
        if(numberOfAnchors < MAX_ANCHORS) {
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
        }
        else{
            //노드가 2개 생성된 상태에서 한번 더 누르게 되면, 그 노드를 위로 이동시킴
            Anchor currentAnchor = selectedAnchorNode.getAnchor();
            //높이노드 생성시와 마찬가지로, 포즈를 가져와서 y를 이동시킨 pose를 만들어줌
            Pose oldPose = currentAnchor.getPose();
            Pose newPose = oldPose.compose(Pose.makeTranslation(0,0.01f,0));
            selectedAnchorNode = moveRenderable(selectedAnchorNode, newPose);
        }
    }

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
