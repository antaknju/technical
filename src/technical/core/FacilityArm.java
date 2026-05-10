package technical.core;

import static mindustry.Vars.tilesize;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import technical.util.TCol;

public class FacilityArm extends FacilityAddapter
{
    public TextureRegion separatorRegion, jointRegion, toolRegion;
    
    public float armLength1 = 25f; 
    public float armLength2 = 25f;
    public float reachRange = 0;
    
    public int separatorsPerSegment = 4;
    public float lineStroke = 3f;
    public float handSpeed = 1f;

    @Override
    public void load() 
    {
        super.load();

        separatorRegion = Core.atlas.find(name + "-separator");
        jointRegion = Core.atlas.find(name + "-joint");
        toolRegion = Core.atlas.find(name + "-tool");
        region = toolRegion;
    }

    public FacilityArm(String name) 
    {
        super(name);

        reachRange = armLength1 + armLength2 - 0.1f;
    }

    @Override
    public TextureRegion[] icons()
    {
        return new TextureRegion[]{baseRegion, toolRegion};
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid)
    {
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, reachRange, Pal.accent);
    }

    public class FacilityArmBuild extends FacilityAddapterBuild
    {
        public Vec2 currentHandPos;

        @Override
        public void created() 
        {
            super.created();
            
            currentHandPos = new Vec2(x, y);
        }

        public void returnToIdle() 
        {
            Vec2 home = Tmp.v1.set(x, y);
            
            if (currentHandPos.dst2(home) > 0.01f)
            {
                currentHandPos.approachDelta(home, handSpeed); 
            } 
            else
            {
                currentHandPos.set(home);
            }
        }

        @Override
        public void updateTile() 
        {
            super.updateTile();

            if (controller() == null || controller().currentStepType() != stepType) 
            {
                returnToIdle();
                return;
            }

            Vec2 center = controller().getFacilityCenter(Tmp.v2);

            if (Mathf.dst2(x, y, center.x, center.y) > reachRange * reachRange)
            {
                returnToIdle();
                return;
            }

            updateAddapter();
        }

        @Override
        public void drawAddapter()
        {
            Vec2 jointPos = calculateIK(x, y, currentHandPos.x, currentHandPos.y, armLength1, armLength2);

            Draw.z(Layer.turret);

            Draw.color(TCol.brass);
            Lines.stroke(lineStroke);
            Lines.line(x, y, jointPos.x, jointPos.y);
            Lines.line(jointPos.x, jointPos.y, currentHandPos.x, currentHandPos.y);
            Draw.color();

            // Segments
            drawSeparators(x, y, jointPos.x, jointPos.y);
            drawSeparators(jointPos.x, jointPos.y, currentHandPos.x, currentHandPos.y);

            // Joints
            Draw.rect(jointRegion, x, y);
            Draw.rect(jointRegion, jointPos.x, jointPos.y);
            Draw.rect(toolRegion, currentHandPos.x, currentHandPos.y, Angles.angle(jointPos.x, jointPos.y, currentHandPos.x, currentHandPos.y) - 90f);
        }

        public float getHandRot()
        {
            Vec2 jointPos = calculateIK(x, y, currentHandPos.x, currentHandPos.y, armLength1, armLength2);

            return Angles.angle(jointPos.x, jointPos.y, currentHandPos.x, currentHandPos.y);
        }

        protected Vec2 calculateIK(float baseX, float baseY, float targetX, float targetY, float l1, float l2) 
        {
            float dst = Mathf.dst(baseX, baseY, targetX, targetY);
            float angleToTarget = Angles.angle(baseX, baseY, targetX, targetY);

            // If target is out of reach, stretch fully in that direction
            if (dst >= l1 + l2) 
            {
                return Tmp.v2.trns(angleToTarget, l1).add(baseX, baseY);
            }

            // Law of Cosines to find the angle of the first joint
            // a^2 = b^2 + c^2 - 2bc*cos(A) -> A = acos((b^2 + c^2 - a^2) / 2bc)
            float cosAngle = (l1 * l1 + dst * dst - l2 * l2) / (2 * l1 * dst);
            float offsetAngle = (float)Math.acos(Mathf.clamp(cosAngle, -1f, 1f)) * Mathf.radDeg;

            // Choose one solution (bend right/left). Subtracting offsetAngle bends one way.
            return Tmp.v2.trns(angleToTarget - offsetAngle, l1).add(baseX, baseY);
        }

        protected void drawSeparators(float x1, float y1, float x2, float y2) 
        {
            float angle = Angles.angle(x1, y1, x2, y2);
            float length = Mathf.dst(x1, y1, x2, y2);
            float step = length / (separatorsPerSegment + 1);

            for (int i = 1; i <= separatorsPerSegment; i++) 
            {
                Tmp.v3.trns(angle, step * i).add(x1, y1);
                Draw.rect(separatorRegion, Tmp.v3.x, Tmp.v3.y, angle - 90f);
            }
        }
    }
}

/*

package technical.expansion;

import static mindustry.Vars.tilesize;

import java.util.Map;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.IntMap;
import arc.struct.IntMap.Entry;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import technical.utility.T;
import technical.utility.TCol;
import technical.utility.Debugger.DebugLine;


public class FacilityArm extends FacilityAddapter
{
    public TextureRegion separatorRegion, jointRegion, toolRegion;
    
    public float segmentLength = 25f;
    public int jointCount = 4;
    public float reachRange;
    
    public int separatorsPerSegment = 4;
    public float segmentStroke = 3f;

    public float handSpeed = 1f;

    @Override
    public void load() 
    {
        super.load();

        separatorRegion = Core.atlas.find(name + "-separator");
        jointRegion = Core.atlas.find(name + "-joint");
        toolRegion = Core.atlas.find(name + "-tool");
        region = toolRegion;
    }

    public FacilityArm(String name) 
    {
        super(name);

        reachRange = segmentLength * (jointCount + 1);
    }

    @Override
    public TextureRegion[] icons()
    {
        return new TextureRegion[]{baseRegion, toolRegion};
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid)
    {
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, reachRange, Pal.accent);
    }

    public class FacilityArmBuild extends FacilityAddapterBuild
    {
        public Vec2 currentHandPos;

        public IntMap<Vec2> joints = new IntMap<>();

        @Override
        public void created() 
        {
            super.created();

            for (int i = 0; i < jointCount; i++) 
            {
                joints.put(i, new Vec2(x + (i * segmentLength), y));
            }

            currentHandPos = new Vec2(x + (jointCount * segmentLength), y);
        }

        public void returnToIdle() 
        {
            Vec2 home = Tmp.v1.set(x, y);
            
            // Use dst() instead of dst2() for a more predictable linear threshold
            if (currentHandPos.dst(home) > 0.1f)
            {
                currentHandPos.approachDelta(home, handSpeed); 
            } 
            else
            {
                currentHandPos.set(home);
            }
            
            solveIK(currentHandPos.x, currentHandPos.y);
        }

        @Override
        public void updateTile()
        {
            super.updateTile();

            // Condition 1: No valid controller or wrong step type
            if (controller() == null || controller().currentStepType() != stepType) 
            {
                returnToIdle();
                return;
            }

            Vec2 center = controller().getFacilityCenter(Tmp.v2);

            // Condition 2: Target is out of range
            if (Mathf.dst2(x, y, center.x, center.y) > reachRange * reachRange)
            {
                returnToIdle();
                updateAddapter(); 
                return;
            }

            // ACTIVATE STATE: Smoothly move the hand towards the facility center
            currentHandPos.approachDelta(center, handSpeed);

            // Calculate joints based on the new hand position
            solveIK(currentHandPos.x, currentHandPos.y);
            
            updateAddapter();
        }

        public void solveIK(float targetX, float targetY) 
        {
            int iterations = 5; // 5 is usually plenty for a simple arm
            float epsilon = 0.5f; // Margin of error
            
            Vec2 target = Tmp.v6.set(targetX, targetY);
            Vec2 base = Tmp.v5.set(x, y);

            // 1. Out of Reach Check (Linear Fallback)
            // If the target is too far, stretch perfectly straight toward it
            if (base.dst(target) > reachRange) {
                Vec2 dir = Tmp.v4.set(target).sub(base).nor();
                for (int i = 0; i < jointCount; i++) {
                    joints.get(i).set(base).add(dir.x * (i * segmentLength), dir.y * (i * segmentLength));
                }
                currentHandPos.approachDelta(joints.get(jointCount - 1), handSpeed);
                return;
            }

            // 2. FABRIK Iterative Solver
            for (int iter = 0; iter < iterations; iter++) {
                
                // --- BACKWARD PASS (Tip to Base) ---
                // Snap the last joint to the target
                joints.get(jointCount - 1).set(target);
                
                // Pull the rest of the joints toward the tip
                for (int i = jointCount - 2; i >= 0; i--) {
                    Vec2 current = joints.get(i);
                    Vec2 next = joints.get(i + 1);
                    
                    // Get direction from 'next' to 'current'
                    Vec2 dir = Tmp.v3.set(current).sub(next);
                    
                    // Anti-overlap safety check!
                    if (dir.len2() < 0.0001f) {
                        dir.set(1, 0); // Give it a fake direction to prevent a crash
                    }
                    
                    // Set distance between joints to exactly segmentLength
                    dir.setLength(segmentLength);
                    current.set(next).add(dir);
                }

                // --- FORWARD PASS (Base to Tip) ---
                // Snap the first joint back to the building's origin (x, y)
                joints.get(0).set(base);
                
                // Pull the rest of the joints toward the base
                for (int i = 1; i < jointCount; i++) {
                    Vec2 current = joints.get(i);
                    Vec2 prev = joints.get(i - 1);
                    
                    // Get direction from 'prev' to 'current'
                    Vec2 dir = Tmp.v2.set(current).sub(prev);
                    
                    // Anti-overlap safety check!
                    if (dir.len2() < 0.0001f) {
                        dir.set(1, 0); 
                    }
                    
                    dir.setLength(segmentLength);
                    current.set(prev).add(dir);
                }

                // Stop calculating early if the tip is practically touching the target
                if (joints.get(jointCount - 1).dst(target) < epsilon) {
                    break;
                }
            }

            // Smoothly update the actual visual hand position
            currentHandPos.approachDelta(joints.get(jointCount - 1), handSpeed);
        }

        @Override
        public void drawAddapter()
        {
            Draw.z(Layer.turret);

            Vec2 base = Tmp.v1.set(x, y);
            Vec2 hand = currentHandPos;

            Draw.color(TCol.brass);
            Lines.stroke(segmentStroke);

            // base -> first joint
            Lines.line(base.x, base.y, joints.get(0).x, joints.get(0).y);

            // joints
            for(int i = 0; i < jointCount - 1; i++)
            {
                Vec2 a = joints.get(i);
                Vec2 b = joints.get(i + 1);

                Lines.line(a.x, a.y, b.x, b.y);
            }

            // last joint -> hand
            Vec2 last = joints.get(jointCount - 1);
            Lines.line(last.x, last.y, hand.x, hand.y);

            Draw.reset();

            // separators
            drawSeparators(base.x, base.y, joints.get(0).x, joints.get(0).y, separatorsPerSegment);

            for(int i = 0; i < jointCount - 1; i++)
            {
                Vec2 a = joints.get(i);
                Vec2 b = joints.get(i + 1);

                drawSeparators(a.x, a.y, b.x, b.y, separatorsPerSegment);
            }

            drawSeparators(last.x, last.y, hand.x, hand.y, separatorsPerSegment);

            // joints
            for(int i = 0; i < jointCount; i++)
            {
                Vec2 j = joints.get(i);
                Draw.rect(jointRegion, j.x, j.y);
            }

            // sattic
            Draw.rect(jointRegion, x, y);
            Draw.rect(toolRegion, hand.x, hand.y, getHandRot());
        }

        public float getHandRot()
        {
            Vec2 last = joints.get(jointCount - 1);
            Vec2 hand = currentHandPos;

            return Angles.angle(last.x, last.y, hand.x, hand.y) - 90f;
        }

        protected void debugLinearJoints(float targetX, float targetY)
        {
            Vec2 base = Tmp.v1.set(x, y);
            Vec2 target = Tmp.v2.set(targetX, targetY);

            int n = jointCount;

            float totalDist = base.dst(target);
            if(totalDist < 0.0001f) return;

            Vec2 dir = target.cpy().sub(base).nor();

            // rzeczywisty krok między punktami (n+1 segmentów)
            float step = totalDist / (n + 1);

            for(int i = 0; i < n; i++)
            {
                float dist = step * (i + 1);
                Vec2 j = joints.get(i);
                j.set(base).mulAdd(dir, dist);
                joints.put(i, j);
            }
        }

        protected void drawSeparators(float x1, float y1, float x2, float y2, int count) 
        {
            float angle = Angles.angle(x1, y1, x2, y2);
            float length = Mathf.dst(x1, y1, x2, y2);
            float step = length / (count + 1);

            for (int i = 1; i <= count; i++) 
            {
                Tmp.v3.trns(angle, step * i).add(x1, y1);
                Draw.rect(separatorRegion, Tmp.v3.x, Tmp.v3.y, angle - 90f);
            }
        }
    }
}
*/