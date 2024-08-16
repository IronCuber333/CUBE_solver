import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Function;

public class CUBE_solver {
    public static void main (String[] args) throws Exception {
        String scramble = "R U R' U'";
        solveCube (scramble, 3, 3, 3, 3);
        
        /*ConstructMove moves = new ConstructMove ();
        Cube cube = new Cube (moves);
        System.out.println (cube.finish ());*/
    }

    static void solveCube (String scramble, int eo, int dr, int htr, int finish) { // キューブを解く.
        ConstructMove moves = new ConstructMove ();
        Cube cube = new Cube (moves);
        cube.doString (scramble);
        cube.historyNormal = new ArrayList<Integer> ();

        // EOの探索.

        List<List<int[][]>> solutionEo = new ArrayList<List<int[][]>> (); // EOの解法. 軸別にまとめる.
        for (int i = 0; i < 3; i++) {
            solutionEo.add (new ArrayList<int[][]> ());
        }

        // 最初からスキップしてたら別扱い.
        boolean skipEo = false; // スキップしてるかどうか.
        if (cube.udEo ()) {
            solutionEo.get (0).add (new int[][] {{}, {}});
            skipEo = true;
        }
        if (cube.fbEo ()) {
            solutionEo.get (1).add (new int[][] {{}, {}});
            skipEo = true;
        }
        if (cube.rlEo ()) {
            solutionEo.get (2).add (new int[][] {{}, {}});
            skipEo = true;
        }
        
        if (skipEo == false) {
            int[] allowedEo = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17}; // EOで用いて良い動き.
            List<Function<Cube, Boolean>> functionEo = new ArrayList<Function<Cube, Boolean>> ();
            functionEo.add (x -> x.udEo ());
            functionEo.add (x -> x.fbEo ());
            functionEo.add (x -> x.rlEo ());
            randomMove (cube, allowedEo, eo, functionEo, solutionEo, true);
        }

        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                System.out.println ("UD軸EO");
            }
            else if (i == 1) {
                System.out.println ("FB軸EO");
            }
            else {
                System.out.println ("RL軸EO");
            }

            int nEo = solutionEo.get (i).size (); // EOの数.
            for (int j = 0; j < nEo; j++) {
                int moveCountNormal = solutionEo.get (i).get (j)[0].length;
                int moveCountInverse = solutionEo.get (i).get (j)[1].length;

                for (int k = 0; k < moveCountNormal; k++) {
                    System.out.print (moves.moveNameSet[solutionEo.get (i).get (j)[0][k]] + " ");
                }
                System.out.print ("(");
                for (int k = 0; k < moveCountInverse; k++) {
                    System.out.print (moves.moveNameSet[solutionEo.get (i).get (j)[1][k]] + " ");
                }
                System.out.println (")");
            }
        }
        
        // DR解法の格納.
        List<int[][]> solutionUdDr = new ArrayList<int[][]> ();
        List<int[][]> solutionFbDr = new ArrayList<int[][]> ();
        List<int[][]> solutionRlDr = new ArrayList<int[][]> ();

        { // UD軸EOからDR.
        
            int[] allowedUdToDr = new int[] {2, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17}; // DRで用いて良い動き.
            List<Function<Cube, Boolean>> functionUdToDr = new ArrayList<Function<Cube, Boolean>> ();
            functionUdToDr.add (x -> x.fbDr ());
            functionUdToDr.add (x -> x.rlDr ());
            List<List<int[][]>> solutionUdToDr = new ArrayList<List<int[][]>> (); // DRの解法.
            for (int i = 0; i < 2; i++) {
                solutionUdToDr.add (new ArrayList<int[][]> ());
            }

            int nUdEo = solutionEo.get (0).size (); // UD軸EOの数.
            for (int i = 0; i < nUdEo; i++) {
                Cube cubeTemp = cube.copyCube (); // キューブをコピーする.
                int moveCountNormal = solutionEo.get (0).get (i)[0].length;
                int moveCountInverse = solutionEo.get (0).get (i)[1].length;

                for (int j = 0; j < moveCountNormal; j++) {
                    cubeTemp.doMove (solutionEo.get (0).get (i)[0][j]);
                }
                cubeTemp.niss (); // インバース方向に.
                for (int j = 0; j < moveCountInverse; j++) {
                    cubeTemp.doMove (solutionEo.get (0).get (i)[1][j]);
                }
                cubeTemp.niss (); // ノーマル方向に戻す.

                // スキップしてたら別扱い.
                boolean skipDr = false; // スキップしてるかどうか.
                if (cubeTemp.fbDr ()) {
                    solutionUdToDr.get (0).add (solutionEo.get (0).get (i));
                    skipDr = true;
                }
                if (cubeTemp.rlDr ()) {
                    solutionUdToDr.get (1).add (solutionEo.get (0).get (i));
                    skipDr = true;
                }
                
                if (skipDr == false) {
                    randomMove (cubeTemp, allowedUdToDr, moveCountNormal + moveCountInverse + dr, functionUdToDr, solutionUdToDr, true);
                }
            }

            solutionFbDr.addAll (solutionUdToDr.get (0));
            solutionRlDr.addAll (solutionUdToDr.get (1));
        }

        { // FB軸EOからDR

            int[] allowedFbToDr = new int[] {0, 1, 2, 3, 4, 5, 8, 11, 12, 13, 14, 15, 16, 17}; // DRで用いて良い動き.
            List<Function<Cube, Boolean>> functionFbToDr = new ArrayList<Function<Cube, Boolean>> ();
            functionFbToDr.add (x -> x.udDr ());
            functionFbToDr.add (x -> x.rlDr ());
            List<List<int[][]>> solutionFbToDr = new ArrayList<List<int[][]>> (); // DRの解法.
            for (int i = 0; i < 2; i++) {
                solutionFbToDr.add (new ArrayList<int[][]> ());
            }

            int nFbEo = solutionEo.get (1).size (); // UD軸EOの数.
            for (int i = 0; i < nFbEo; i++) {
                Cube cubeTemp = cube.copyCube (); // キューブをコピーする.
                int moveCountNormal = solutionEo.get (1).get (i)[0].length;
                int moveCountInverse = solutionEo.get (1).get (i)[1].length;

                for (int j = 0; j < moveCountNormal; j++) {
                    cubeTemp.doMove (solutionEo.get (1).get (i)[0][j]);
                }
                cubeTemp.niss (); // インバース方向に.
                for (int j = 0; j < moveCountInverse; j++) {
                    cubeTemp.doMove (solutionEo.get (1).get (i)[1][j]);
                }
                cubeTemp.niss (); // ノーマル方向に戻す.
                
                // スキップしてたら別扱い.
                boolean skipDr = false; // スキップしてるかどうか.
                if (cubeTemp.udDr ()) {
                    solutionFbToDr.get (0).add (solutionEo.get (1).get (i));
                    skipDr = true;
                }
                if (cubeTemp.rlDr ()) {
                    solutionFbToDr.get (1).add (solutionEo.get (1).get (i));
                    skipDr = true;
                }

                if (skipDr == false) {
                    randomMove (cubeTemp, allowedFbToDr, moveCountNormal + moveCountInverse + dr, functionFbToDr, solutionFbToDr, true);
                }
            }

            solutionUdDr.addAll (solutionFbToDr.get (0));
            solutionRlDr.addAll (solutionFbToDr.get (1));
        }

        { // RL軸EOからDR

            int[] allowedRlToDr = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 14, 17}; // DRで用いて良い動き.
            List<Function<Cube, Boolean>> functionRlToDr = new ArrayList<Function<Cube, Boolean>> ();
            functionRlToDr.add (x -> x.udDr ());
            functionRlToDr.add (x -> x.fbDr ());
            List<List<int[][]>> solutionRlToDr = new ArrayList<List<int[][]>> (); // DRの解法.
            for (int i = 0; i < 2; i++) {
                solutionRlToDr.add (new ArrayList<int[][]> ());
            }

            int nRlEo = solutionEo.get (2).size (); // RL軸EOの数.
            for (int i = 0; i < nRlEo; i++) {
                Cube cubeTemp = cube.copyCube (); // キューブをコピーする.
                int moveCountNormal = solutionEo.get (2).get (i)[0].length;
                int moveCountInverse = solutionEo.get (2).get (i)[1].length;

                for (int j = 0; j < moveCountNormal; j++) {
                    cubeTemp.doMove (solutionEo.get (2).get (i)[0][j]);
                }
                cubeTemp.niss (); // インバース方向に.
                for (int j = 0; j < moveCountInverse; j++) {
                    cubeTemp.doMove (solutionEo.get (2).get (i)[1][j]);
                }
                cubeTemp.niss (); // ノーマル方向に戻す.

                // スキップしてたら別扱い.
                boolean skipDr = false; // スキップしてるかどうか.
                if (cubeTemp.udDr ()) {
                    solutionRlToDr.get (0).add (solutionEo.get (2).get (i));
                    skipDr = true;
                }
                if (cubeTemp.fbDr ()) {
                    solutionRlToDr.get (1).add (solutionEo.get (2).get (i));
                    skipDr = true;
                }
                
                if (skipDr == false) {
                    randomMove (cubeTemp, allowedRlToDr, moveCountNormal + moveCountInverse + dr, functionRlToDr, solutionRlToDr, true);
                }
            }

            solutionUdDr.addAll (solutionRlToDr.get (0));
            solutionFbDr.addAll (solutionRlToDr.get (1));
        }

        int nUdDr = solutionUdDr.size (); // UD軸DRの数.
        System.out.println ("UD軸DR");
        for (int i = 0; i < nUdDr; i++) {
            int moveCountNormal = solutionUdDr.get (i)[0].length;
            int moveCountInverse = solutionUdDr.get (i)[1].length;

            for (int j = 0; j < moveCountNormal; j++) {
                System.out.print (moves.moveNameSet[solutionUdDr.get (i)[0][j]] + " ");
            }
            System.out.print ("(");
            for (int j = 0; j < moveCountInverse; j++) {
                System.out.print (moves.moveNameSet[solutionUdDr.get (i)[1][j]] + " ");
            }
            System.out.println (")");
        }

        int nFbDr = solutionFbDr.size (); // FB軸DRの数.
        System.out.println ("FB軸DR");
        for (int i = 0; i < nFbDr; i++) {
            int moveCountNormal = solutionFbDr.get (i)[0].length;
            int moveCountInverse = solutionFbDr.get (i)[1].length;

            for (int j = 0; j < moveCountNormal; j++) {
                System.out.print (moves.moveNameSet[solutionFbDr.get (i)[0][j]] + " ");
            }
            System.out.print ("(");
            for (int j = 0; j < moveCountInverse; j++) {
                System.out.print (moves.moveNameSet[solutionFbDr.get (i)[1][j]] + " ");
            }
            System.out.println (")");
        }

        int nRlDr = solutionRlDr.size (); // RL軸DRの数.
        System.out.println ("RL軸DR");
        for (int i = 0; i < nRlDr; i++) {
            int moveCountNormal = solutionRlDr.get (i)[0].length;
            int moveCountInverse = solutionRlDr.get (i)[1].length;

            for (int j = 0; j < moveCountNormal; j++) {
                System.out.print (moves.moveNameSet[solutionRlDr.get (i)[0][j]] + " ");
            }
            System.out.print ("(");
            for (int j = 0; j < moveCountInverse; j++) {
                System.out.print (moves.moveNameSet[solutionRlDr.get (i)[1][j]] + " ");
            }
            System.out.println (")");
        }

        List<int[][]> solutionHtr = new ArrayList<int[][]> (); // HTRの解法.

        { // UD軸DRからHTR

            int[] allowedUdToHtr = new int[] {0, 1, 2, 3, 4, 5, 8, 11, 14, 17}; // DRで用いて良い動き.
            List<Function<Cube, Boolean>> functionUdToHtr = new ArrayList<Function<Cube, Boolean>> ();
            functionUdToHtr.add (x -> x.htr ());
            List<List<int[][]>> solutionUdToHtr = new ArrayList<List<int[][]>> (); // DRの解法.
            solutionUdToHtr.add (new ArrayList<int[][]> ());

            for (int i = 0; i < nUdDr; i++) {
                Cube cubeTemp = cube.copyCube (); // キューブをコピーする.
                int moveCountNormal = solutionUdDr.get (i)[0].length;
                int moveCountInverse = solutionUdDr.get (i)[1].length;

                for (int j = 0; j < moveCountNormal; j++) {
                    cubeTemp.doMove (solutionUdDr.get (i)[0][j]);
                }
                cubeTemp.niss (); // インバース方向に.
                for (int j = 0; j < moveCountInverse; j++) {
                    cubeTemp.doMove (solutionUdDr.get (i)[1][j]);
                }
                cubeTemp.niss (); // ノーマル方向に戻す.

                // スキップしていたら別扱い.
                if (cubeTemp.htr ()) {
                    solutionUdToHtr.get (0).add (solutionUdDr.get (i));
                }
                else {
                    randomMove (cubeTemp, allowedUdToHtr, moveCountNormal + moveCountInverse + htr, functionUdToHtr, solutionUdToHtr, true);
                }
            }

            solutionHtr.addAll (solutionUdToHtr.get (0));
        }

        { // FB軸DRからHTR

            int[] allowedFbToHtr = new int[] {2, 5, 6, 7, 8, 9, 10, 11, 14, 17}; // DRで用いて良い動き.
            List<Function<Cube, Boolean>> functionFbToHtr = new ArrayList<Function<Cube, Boolean>> ();
            functionFbToHtr.add (x -> x.htr ());
            List<List<int[][]>> solutionFbToHtr = new ArrayList<List<int[][]>> (); // DRの解法.
            solutionFbToHtr.add (new ArrayList<int[][]> ());

            for (int i = 0; i < nFbDr; i++) {
                Cube cubeTemp = cube.copyCube (); // キューブをコピーする.
                int moveCountNormal = solutionFbDr.get (i)[0].length;
                int moveCountInverse = solutionFbDr.get (i)[1].length;

                for (int j = 0; j < moveCountNormal; j++) {
                    cubeTemp.doMove (solutionFbDr.get (i)[0][j]);
                }
                cubeTemp.niss (); // インバース方向に.
                for (int j = 0; j < moveCountInverse; j++) {
                    cubeTemp.doMove (solutionFbDr.get (i)[1][j]);
                }
                cubeTemp.niss (); // ノーマル方向に戻す.

                // スキップしていたら別扱い.
                if (cubeTemp.htr ()) {
                    solutionFbToHtr.get (0).add (solutionFbDr.get (i));
                }
                else {
                    randomMove (cubeTemp, allowedFbToHtr, moveCountNormal + moveCountInverse + htr, functionFbToHtr, solutionFbToHtr, true);
                }
            }

            solutionHtr.addAll (solutionFbToHtr.get (0));
        }

        { // RL軸DRからHTR

            int[] allowedRlToHtr = new int[] {2, 5, 8, 11, 12, 13, 14, 15, 16, 17}; // DRで用いて良い動き.
            List<Function<Cube, Boolean>> functionRlToHtr = new ArrayList<Function<Cube, Boolean>> ();
            functionRlToHtr.add (x -> x.htr ());
            List<List<int[][]>> solutionRlToHtr = new ArrayList<List<int[][]>> (); // DRの解法.
            solutionRlToHtr.add (new ArrayList<int[][]> ());

            for (int i = 0; i < nFbDr; i++) {
                Cube cubeTemp = cube.copyCube (); // キューブをコピーする.
                int moveCountNormal = solutionRlDr.get (i)[0].length;
                int moveCountInverse = solutionRlDr.get (i)[1].length;

                for (int j = 0; j < moveCountNormal; j++) {
                    cubeTemp.doMove (solutionRlDr.get (i)[0][j]);
                }
                cubeTemp.niss (); // インバース方向に.
                for (int j = 0; j < moveCountInverse; j++) {
                    cubeTemp.doMove (solutionRlDr.get (i)[1][j]);
                }
                cubeTemp.niss (); // ノーマル方向に戻す.

                // スキップしていたら別扱い.
                if (cubeTemp.htr ()) {
                    solutionRlToHtr.get (0).add (solutionRlDr.get (i));
                }
                else {
                    randomMove (cubeTemp, allowedRlToHtr, moveCountNormal + moveCountInverse + htr, functionRlToHtr, solutionRlToHtr, true);
                }
            }

            solutionHtr.addAll (solutionRlToHtr.get (0));
        }

        System.out.println ("HTR");
        int nHtr = solutionHtr.size ();
        for (int i = 0; i < nHtr; i++) {
            int moveCountNormal = solutionHtr.get (i)[0].length;
            int moveCountInverse = solutionHtr.get (i)[1].length;

            for (int j = 0; j < moveCountNormal; j++) {
                System.out.print (moves.moveNameSet[solutionHtr.get (i)[0][j]] + " ");
            }
            System.out.print ("(");
            for (int j = 0; j < moveCountInverse; j++) {
                System.out.print (moves.moveNameSet[solutionHtr.get (i)[1][j]] + " ");
            }
            System.out.println (")");
        }

        List<List<int[][]>> solutionFinish = new ArrayList<List<int[][]>> (); // Finishの解法.
        solutionFinish.add (new ArrayList<int[][]> ());

        { // Finish

            int[] allowedFinish = new int[] {2, 5, 8, 11, 14, 17}; // Finishで用いて良い動き.
            List<Function<Cube, Boolean>> functionFinish = new ArrayList<Function<Cube, Boolean>> ();
            functionFinish.add (x -> x.finish ());

            for (int i = 0; i < nHtr; i++) {
                Cube cubeTemp = cube.copyCube (); // キューブをコピーする.
                int moveCountNormal = solutionHtr.get (i)[0].length;
                int moveCountInverse = solutionHtr.get (i)[1].length;

                for (int j = 0; j < moveCountNormal; j++) {
                    cubeTemp.doMove (solutionHtr.get (i)[0][j]);
                }
                cubeTemp.niss (); // インバース方向に.
                for (int j = 0; j < moveCountInverse; j++) {
                    cubeTemp.doMove (solutionHtr.get (i)[1][j]);
                }
                cubeTemp.niss (); // ノーマル方向に戻す.

                // スキップしていたら別扱い.
                if (cubeTemp.finish ()) {
                    solutionFinish.get (0).add (solutionHtr.get (i));
                }
                else {
                    randomMove (cubeTemp, allowedFinish, moveCountNormal + moveCountInverse + finish, functionFinish, solutionFinish, false);
                }
            }
        }

        System.out.println ("Finish");
        int nFinish = solutionFinish.get (0).size ();
        for (int i = 0; i < nFinish; i++) {
            int moveCountNormal = solutionFinish.get (0).get (i)[0].length;
            int moveCountInverse = solutionFinish.get (0).get (i)[1].length;

            for (int j = 0; j < moveCountNormal; j++) {
                System.out.print (moves.moveNameSet[solutionFinish.get (0).get (i)[0][j]] + " ");
            }
            System.out.print ("(");
            for (int j = 0; j < moveCountInverse; j++) {
                System.out.print (moves.moveNameSet[solutionFinish.get (0).get (i)[1][j]] + " ");
            }
            System.out.println (")");
        }

        // 解法の表示.

        System.out.println ("解法");
        for (int i = 1; i < nFinish; i++) {
            int moveCountNormal = solutionFinish.get (0).get (i)[0].length;
            int moveCountInverse = solutionFinish.get (0).get (i)[1].length;

            for (int j = 0; j < moveCountNormal; j++) {
                System.out.print (moves.moveNameSet[solutionFinish.get (0).get (i)[0][j]] + " ");
            }
            for (int j = 0; j < moveCountInverse; j++) {
                if (solutionFinish.get (0).get (i)[1][moveCountInverse - j - 1] % 3 == 0) { // 時計回り.
                    System.out.print (moves.moveNameSet[solutionFinish.get (0).get (i)[1][moveCountInverse - j - 1] + 1] + " ");
                }
                else if (solutionFinish.get (0).get (i)[1][moveCountInverse - j - 1] % 3 == 1) { // 反時計回り.
                    System.out.print (moves.moveNameSet[solutionFinish.get (0).get (i)[1][moveCountInverse - j - 1] - 1] + " ");
                }
                else { // 180度回転の場合.
                    System.out.print (moves.moveNameSet[solutionFinish.get (0).get (i)[1][moveCountInverse - j - 1]] + " ");
                }
            }
            
            System.out.println ();
        }

    }

    static void randomMove (
        Cube cube, // キューブ.
        int[] allowedMove, // 許される回転.
        int allowedMoveCount, // 許される手数.
        List<Function<Cube, Boolean>> functions, // 判定に必要な関数.
        List<List<int[][]>> solutions, // 解法リスト.
        boolean ifNiss // NISSするか.
    ) { // ランダムな動きをして, 条件を満たすか確かめる.
        int moveCount = cube.moveCount (); // すでに課されている手数.

        if (moveCount < allowedMoveCount) { // まだ手数をかけられる場合.
            int nMove = allowedMove.length; // 回転の種類数.
            int nFunction = functions.size (); // 判定に必要な関数の個数.

            List<Integer> formerHistory; // 前までの動き. ノーマル方向ならノーマルの回転記号, インバース方向ならインバースの回転記号になる.
            if (cube.ifNormal) {
                formerHistory = cube.historyNormal;
            }
            else {
                formerHistory = cube.historyInverse;
            }
            int formerMoveCount = formerHistory.size (); // 今の方向においてすでになされた手数.

            for (int i = 0; i < nMove; i++) {
                if (formerMoveCount >= 1) { // すでに何かの動きがされている場合.
                    if ((allowedMove[i] / 3) == (formerHistory.get (formerMoveCount - 1) / 3)) { // 直前と同じ面の動きはしてはいけない.
                        continue;
                    }
                    if (
                        ((allowedMove[i] / 6) == (formerHistory.get (formerMoveCount - 1) / 6)) &&
                        ((formerHistory.get (formerMoveCount - 1) / 3) - (allowedMove[i] / 3) == 1)
                    ) { // 反対側の面は優先順位をつける.
                        continue;
                    }
                }
                if (formerMoveCount >= 2) { // すでに2手以上されている場合.
                    if (
                        ((allowedMove[i] / 6) == (formerHistory.get (formerMoveCount - 1) / 6)) &&
                        ((allowedMove[i] / 6) == (formerHistory.get (formerMoveCount - 2) / 6))
                    ){ // U D Uのようになってはいけない.
                        continue;
                    }
                }

                Cube cubeTemp = cube.copyCube (); // キューブをコピーする.
                cubeTemp.doMove (allowedMove[i]);
                boolean ifSolved = false; // 条件を満たしているか.
                
                for (int j = 0; j < nFunction; j++) {
                    if (functions.get (j).apply (cubeTemp)) { // キューブが条件を満たしたら.
                        // 手数.
                        int moveCountNormal = cubeTemp.historyNormal.size ();
                        int moveCountInverse = cubeTemp.historyInverse.size ();
                        // 解法.
                        int[] solutionNormal = new int[moveCountNormal];
                        int[] solutionInverse = new int[cubeTemp.historyInverse.size ()];
                        
                        // historyの内容をコピー.
                        for (int k = 0; k < moveCountNormal; k++) {
                            solutionNormal[k] = cubeTemp.historyNormal.get (k);
                        }
                        for (int k = 0; k < moveCountInverse; k++) {
                            solutionInverse[k] = cubeTemp.historyInverse.get (k);
                        }

                        ifSolved = true;
                        solutions.get (j).add (new int[][] {solutionNormal, solutionInverse});
                    }
                }

                if (ifSolved == false) {
                    randomMove (cubeTemp, allowedMove, allowedMoveCount, functions, solutions, ifNiss);
                }
            }

            if (ifNiss) { // NISSするなら.
                if (cube.ifNormal) { // ノーマル方向であればNISSする.
                    Cube cubeTemp = cube.copyCube ();
                    cubeTemp.niss ();
                    randomMove (cubeTemp, allowedMove, allowedMoveCount, functions, solutions, ifNiss);
                }
            }
        }
    }
}

class Cube {
    int[] ep; // エッジキューブの位置.
    int[] eo; // エッジキューブの向き.
    int[] cp; // コーナーキューブの位置.
    int[] co; // コーナーキューブの向き.

    ConstructMove moves; // Moveの集合体.
    
    // 過去の動き.
    List<Integer> historyNormal;
    List<Integer> historyInverse;
    boolean ifNormal; // ノーマル方向かどうか.

    Cube (ConstructMove moves) {
        // 初期状態.
        ep = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        eo = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        cp = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
        co = new int[] {0, 0, 0, 0, 0, 0, 0, 0};

        this.moves = moves;

        historyNormal = new ArrayList<Integer> ();
        historyInverse = new ArrayList<Integer> ();

        ifNormal = true;
    }

    void doMove (int i) { // iに対応する回転をする.
        ep = General.permutate (ep, moves.moveSet[i].ep);
        eo = General.permutate (General.sumMod (eo, moves.moveSet[i].eo, 2), moves.moveSet[i].ep);
        cp = General.permutate (cp, moves.moveSet[i].cp);
        co = General.permutate (General.sumMod (co, moves.moveSet[i].co, 3), moves.moveSet[i].cp);
        
        if (ifNormal) { // ノーマル方向の場合.
            historyNormal.add (i);
        }
        else { // インバース方向の場合.
            historyInverse.add (i);
        }
    }

    void doString (String moveString) {
        String[] moveArray = moveString.split (" ");
        int n = moveArray.length; // 手数.

        for (int i = 0; i < n; i++) {
            doMove (stringToMove (moveArray[i]));
        }
    }

    int stringToMove (String x) { // 回転記号xに対応する回転番号を求める.
        int k = moves.moveNameSet.length; // 回転の種類数.

        for (int i = 0; i < k; i++) {
            if (moves.moveNameSet[i].equals (x)) {
                return i;
            }
        }
        return moves.moveNameSet.length; // zeroMoveの番号.
    }

    void niss () { // NISSする.
        int[] epTemp = General.inverse (ep);
        int[] eoTemp = General.scalarMod ((General.permutate (eo, General.inverse (ep))), -1, 2);
        int[] cpTemp = General.inverse (cp);
        int[] coTemp = General.scalarMod ((General.permutate (co, General.inverse (cp))), -1, 3);
        
        ep = epTemp;
        eo = eoTemp;
        cp = cpTemp;
        co = coTemp;
        
        ifNormal = !ifNormal;
    }

    int moveCount () { // すでになされた手数を返す.
        return historyNormal.size () + historyInverse.size ();
    }

    void show () { // キューブを表示する.
        System.out.println ("エッジキューブの位置: " + Arrays.toString (ep));
        System.out.println ("エッジキューブの向き: " + Arrays.toString (eo));
        System.out.println ("コーナーキューブの位置: " + Arrays.toString (cp));
        System.out.println ("コーナーキューブの向き: " + Arrays.toString (co));
    }

    boolean udEo () { // UD軸のEOが完成しているか確かめる.
        for (int i = 0; i < 12; i++) {
            if ((i == 0) || (i == 2) || (i == 8) || (i == 10)) { // M層にあるエッジについて.
                if ((ep[i] == 0) || (ep[i] == 2) || (ep[i] == 8) || (ep[i] == 10)) { // M層にM層エッジがある場合.
                    if (eo[i] != 0) { // 向きが0でなければならない.
                        return false;
                    }
                }
                else { // M層にRL面エッジがある場合.
                    if (eo[i] != 1) { // 向きが1でなければならない.
                        return false;
                    }
                }
            }
            else { // RL面にあるエッジについて.
                if ((ep[i] == 0) || (ep[i] == 2) || (ep[i] == 8) || (ep[i] == 10)) { // RL面にM層エッジがある場合.
                    if (eo[i] != 1) { // 向きが1でなければならない.
                        return false;
                    }
                }
                else { // RL面にRL面エッジがある場合
                    if (eo[i] != 0) { // 向きが0でなければならない.
                        return false;
                    }
                }
            }
        }

        return true; // すべてOKならtrue;
    }

    boolean fbEo () { // FB軸のEOが完成しているか確かめる.
        return Arrays.equals (eo, new int[12]); // eoの定義そのもの.
    }

    boolean rlEo () { // RL軸EOが完成しているか確かめる.
        for (int i = 0; i < 12; i++) {
            if ((i >= 4) && (i <= 7)) { // E層にあるエッジについて.
                if ((ep[i] >= 4) && (ep[i] <= 7)) { // E層にE層エッジがある場合.
                    if (eo[i] != 0) { // 向きが0でなければならない.
                        return false;
                    }
                }
                else { // E層にUD面エッジがある場合.
                    if (eo[i] != 1) { // 向きが1でなければならない.
                        return false;
                    }
                }
            }
            else { // UD面にあるエッジについて.
                if ((ep[i] >= 4) && (ep[i] <= 7)) { // UD面にE層エッジがある場合.
                    if (eo[i] != 1) { // 向きが1でなければならない.
                        return false;
                    }
                }
                else { // UD面にUD面エッジがある場合.
                    if (eo[i] != 0) { // 向きが0でなければならない.
                        return false;
                    }
                }
            }
        }

        return true; // すべてOKならばtrue;
    }

    boolean udDr () { // UD軸DRが完成しているか確かめる.
        // エッジの向き.
        if (fbEo () == false) { // FB軸EOができているか.
            return false;
        }

        // エッジの位置.
        for (int i = 4; i <= 7; i++) { // E層エッジ.
            if ((ep[i] <= 3) || (ep[i] >= 8)) { // E層にE層でないエッジがあってはいけない.
                return false;
            }
        }

        // コーナーの向き.
        if (Arrays.equals (co, new int[8]) == false) { // コーナーの向きが合っているか.
            return false;
        }        

        return true; // すべてOKならばtrue;
    }

    boolean fbDr () { // FB軸DRが完成しているか確かめる.
        // エッジの向き.        
        if (udEo () == false) { // UD軸EOができているか.
            return false;
        }

        // エッジの位置.
        for (int i = 0; i < 12; i++) {
            if ((i == 1) || (i == 3) || (i == 9) || (i == 11)) { // S層エッジ.
                if ((ep[i] != 1) && (ep[i] != 3) && (ep[i] != 9) && (ep[i] != 11)) { // S層にS層ではないエッジがあってはいけない.
                    return false;
                }
            }
        }

        // コーナーの向き.
        for (int i = 0; i < 8; i++) {
            if ((i % 2) == 0) { // 偶数位置(FB面が2側についている位置).
                if ((cp[i] % 2) == 0) { // 偶数位置に偶数コーナーがある場合.
                    if (co[i] != 0) { // コーナーの向きが0でなければならない.
                        return false;
                    }
                }
                else { // 偶数位置に奇数コーナーがある場合.
                    if (co[i] != 1) { // コーナーの向きが1でなければならない.
                        return false;
                    } 
                }
            }
            else { // 奇数位置(FB面が1側についている位置)
                if ((cp[i] % 2) == 0) { // 奇数位置に偶数コーナーがある場合.
                    if (co[i] != 2) { // コーナーの向きが2でなければならない.
                        return false;
                    }
                }
                else { // 奇数位置に奇数コーナーがある場合.
                    if (co[i] != 0) { // コーナーの向きが0でなければならない.
                        return false;
                    }
                }
            }
        }

        return true; // すべてOKならばtrue;
    }

    boolean rlDr () { // RL軸DRが完成しているか確かめる.
        // エッジの向き.        
        if (fbEo () == false) { // FB軸EOができているか.
            return false;
        }

        // エッジの位置.
        for (int i = 0; i < 12; i++) {
            if ((i == 0) || (i == 2) || (i == 8) || (i == 10)) { // M層エッジ.
                if ((ep[i] != 0) && (ep[i] != 2) && (ep[i] != 8) && (ep[i] != 10)) { // M層にM層ではないエッジがあってはいけない.
                    return false;
                }
            }
        }

        // コーナーの向き.
        for (int i = 0; i < 8; i++) {
            if ((i % 2) == 0) { // 偶数位置(RL面が1側についている位置).
                if ((cp[i] % 2) == 0) { // 偶数位置に偶数コーナーがある場合.
                    if (co[i] != 0) { // コーナーの向きが0でなければならない.
                        return false;
                    }
                }
                else { // 偶数位置に奇数コーナーがある場合.
                    if (co[i] != 2) { // コーナーの向きが2でなければならない.
                        return false;
                    } 
                }
            }
            else { // 奇数位置(RL面が2側についている位置)
                if ((cp[i] % 2) == 0) { // 奇数位置に偶数コーナーがある場合.
                    if (co[i] != 1) { // コーナーの向きが1でなければならない.
                        return false;
                    }
                }
                else { // 奇数位置に奇数コーナーがある場合.
                    if (co[i] != 0) { // コーナーの向きが0でなければならない.
                        return false;
                    }
                }
            }
        }

        return true; // すべてOKならばtrue;
    }

    boolean htr () { // HTRが完成しているか確かめる.
        // UD軸DRができているかどうか.
        if (udDr () == false) {
            return false;
        }

        // 偶数コーナーと奇数コーナーがそれぞれ閉じているか.
        for (int i = 0; i < 8; i++) {
            if ((i % 2) != (cp[i] % 2)) {
                return false;
            }
        }

        // UD軸が揃っているか.
        int countUd = 0; // 両方とも白の組の個数.
        for (int i = 0; i <= 3; i++) {
            if ((cp[i] <= 3) && (cp[7 - i] <= 3)) {
                countUd++; // どちらも白ならカウント.
            }
        }
        if (countUd == 1) {
            return false;
        }

        // FB軸が揃っているかどうか.
        int countFb = 0; // 両方とも緑の組の個数.
        for (int i = 2; i <= 5; i++) {
            if ((cp[i] >= 2) && (cp[i] <= 5) && (cp[General.mod (3 - i, 8)] >= 2) && (cp[General.mod (3 - i, 8)] <= 5)) {
                countFb++; // どちらも緑ならカウント.
            }
        }
        if (countFb == 1) {
            return false;
        }
        
        return true;
    }

    boolean finish () { // 揃っているかどうか確かめる.
        for (int i = 0; i < 12; i++) {
            if (ep[i] != i) {
                return false;
            }
            if (eo[i] != 0) {
                return false;
            }
        }

        for (int i = 0; i < 8; i++) {
            if (cp[i] != i) {
                return false;
            }
            if (co[i] != 0) {
                return false;
            }
        }

        return true;
    }

    Cube copyCube () {
        Cube cube = new Cube (moves);
        cube.ep = ep;
        cube.eo = eo;
        cube.cp = cp;
        cube.co = co;
        
        cube.historyNormal = new ArrayList<Integer> (historyNormal);
        cube.historyInverse = new ArrayList<Integer> (historyInverse);
        cube.ifNormal = ifNormal;

        return cube;
    }
}

class ConstructMove {

    Move zeroMove; // 何もしない動き.

    Move[] moveSet; // Moveの集合体.
    String[] moveNameSet; // 回転記号の集合体. moveSetと対応.

    ConstructMove () { // 各回転の定義.

        // 各回転の宣言.
        Move u, uTwo, uPrime, d, dTwo, dPrime, f, fTwo, fPrime, b, bTwo, bPrime, r, rTwo, rPrime, l, lTwo, lPrime, zeroMove;
        
        // 回転の定義.
        u = new Move (
            General.permutatePart (12, new int[][] {{0, 1, 2, 3}}),
            new int[12],
            General.permutatePart (8, new int[][] {{0, 1, 2, 3}}),
            new int[8]
        );
        uTwo = General.combineMove (u, u);
        uPrime = General.inverseMove (u);
        d = new Move (
            General.permutatePart (12, new int[][] {{8, 9, 10, 11}}),
            new int[12],
            General.permutatePart (8, new int[][] {{4, 5, 6, 7}}),
            new int[8]
        );
        dTwo = General.combineMove (d, d);
        dPrime = General.inverseMove (d);
        f = new Move (
            General.permutatePart (12, new int[][] {{2, 4, 8, 5}}),
            General.orientPart (12, new int[] {2, 4, 8, 5}, new int[] {1, 1, 1, 1}),
            General.permutatePart (8, new int[][] {{3, 2, 5, 4}}),
            General.orientPart (8, new int[] {3, 2, 5, 4}, new int[] {1, 2, 1, 2})
        );
        fTwo = General.combineMove (f, f);
        fPrime = General.inverseMove (f);
        b = new Move (
            General.permutatePart (12, new int[][] {{0, 6, 10, 7}}),
            General.orientPart (12, new int[] {0, 6, 10, 7}, new int[] {1, 1, 1, 1}),
            General.permutatePart (8, new int[][] {{1, 0, 7, 6}}),
            General.orientPart (8, new int[] {1, 0, 7, 6}, new int[] {1, 2, 1, 2})
        );
        bTwo = General.combineMove (b, b);
        bPrime = General.inverseMove (b);
        r = new Move (
            General.permutatePart (12, new int[][] {{1, 7, 9, 4}}),
            new int[12],
            General.permutatePart (8, new int[][] {{2, 1, 6, 5}}),
            General.orientPart (8, new int[] {2, 1, 6, 5}, new int[] {1, 2, 1, 2})
        );
        rTwo = General.combineMove (r, r);
        rPrime = General.inverseMove (r);
        l = new Move (
            General.permutatePart (12, new int[][] {{3, 5, 11, 6}}),
            new int[12],
            General.permutatePart (8, new int[][] {{0, 3, 4, 7}}),
            General.orientPart (8, new int[] {0, 3, 4, 7}, new int[] {1, 2, 1, 2})
        );
        lTwo = General.combineMove (l, l);
        lPrime = General.inverseMove (l);

        zeroMove = new Move (
            General.permutatePart (12, new int[][] {}),
            new int[12],
            General.permutatePart (8, new int[][] {}),
            new int[8]
        );

        moveSet = new Move[] {
            u, uPrime, uTwo, d, dPrime, dTwo,
            f, fPrime, fTwo, b, bPrime, bTwo,
            r, rPrime, rTwo, l, lPrime, lTwo,
            zeroMove
        };

        moveNameSet = new String[] {
            "U", "U'", "U2", "D", "D'", "D2",
            "F", "F'", "F2", "B", "B'", "B2",
            "R", "R'", "R2", "L", "L'", "L2"
        };
    }
}

class Move { // 回転を表すオブジェクト.
    int[] ep; // エッジの位置の変化.
    int[] eo; // エッジの向きの変化.
    int[] cp; // コーナーの位置の変化.
    int[] co; // コーナーの向きの変化.

    Move (int[] ep, int[] eo, int[] cp, int[] co) {
        this.ep = ep;
        this.eo = eo;
        this.cp = cp;
        this.co = co;
    }
}

class General {
    static int[] permutate (int[] a, int[] b) { // ベクトルaを置換bにかける.
        int n = a.length; // 要素数.
        int[] ab = new int[n]; // 置換後の配列.

        for (int i = 0; i < n; i++) {
            ab[i] = a[b[i]];
        }

        return ab;
    }

    static int[] inverse (int[] a) { // 置換aの逆置換を求める.
        int n = a.length; // 要素数.
        int[] aInverse = new int[n]; // 逆置換.

        for (int i = 0; i < n; i++) {
            aInverse[a[i]] = i;
        }

        return aInverse;
    }

    static int mod (int n, int m) { // nをmで割った余りを求める.
        int modValue = n % m; // 余りだが, nが負の場合負になってしまう.

        if (modValue < 0) {
            modValue += m; // 負の場合はmを足す.
        }

        return modValue;
    }

    static int[] sumMod (int[] a, int[] b, int m) { // ベクトルaとbを足す. ただし, mod mで整える.
        int n = a.length; // 要素数.
        int[] aPlusb = new int[n]; // a+b.

        for (int i = 0; i < n; i++) {
            aPlusb[i] = mod ((a[i] + b[i]), m);
        }

        return aPlusb;
    }

    static int[] scalarMod (int[] a, int k, int m) { // ベクトルのk倍を求める. ただし, mod mで整える.
        int n = a.length; // 要素数.
        int[] ka = new int[n] ; // aのk倍.

        for (int i = 0; i < n; i++) {
            ka[i] = mod ((a[i] * k), m);
        }

        return ka;
    }

    static int[] permutatePart (int n, int[][] part) { // 特定の部分(part)の要素を順繰りに交換する. 例:{0,1,2,3}→{3,0,1,2}
        int[] permutated = new int[n]; // 交換された置換.
        
        for (int i = 0; i < n; i++) { // まずは交換されていない置換.
            permutated[i] = i;
        }

        int k = part.length; // 順繰りの個数.

        for (int i = 0; i < k; i++) {
            int l = part[i].length; // 順繰りの大きさ.

            for (int j = 0; j < l; j++) {
                permutated[part[i][(j + 1) % l]] = part[i][j];
            }
        }

        return permutated;
    }

    static int[] orientPart (int n, int[] part, int[] orient) { // 特定の部分(part)をorientだけ向きを変える.
        int[] oriented = new int[n];
        int k = part.length;

        for (int i = 0; i < k; i++) {
            oriented[part[i]] = orient[i]; 
        }

        return oriented;
    }

    static Move combineMove (Move a, Move b) { // aとbを組み合わせる.
        int[] ep = permutate (a.ep, b.ep); // 組み合わせたあとのエッジの位置の変化.
        int[] eo = sumMod (a.eo, permutate (b.eo, inverse (a.ep)), 2); // 組み合わせたあとのエッジの向きの変化
        int[] cp = permutate (a.cp, b.cp); // 組み合わせたあとのコーナーの位置の変化.
        int[] co = sumMod (a.co, permutate (b.co, inverse (a.cp)), 3); // 組み合わせたあとのコーナーの向きの変化.

        return new Move (ep, eo, cp, co); // 組み合わせた動き.
    }

    static Move inverseMove (Move a) { // aの逆操作.
        int[] ep = inverse (a.ep); // 逆操作のエッジの位置の変化.
        int[] eo = scalarMod (permutate (a.eo, a.ep), -1, 2); // 逆操作のエッジの向きの変化.
        int[] cp = inverse (a.cp); // 逆操作のコーナーの位置の変化.
        int[] co = scalarMod (permutate (a.co, a.cp), -1, 3); // 逆操作のコーナーの向きの変化.

        return new Move (ep, eo, cp, co); // 逆操作.
    }
}