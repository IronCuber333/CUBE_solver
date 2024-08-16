# ルービックキューブキューブソルバー

ルービックキューブを解くプログラムを作成しました．少し時間はかかりますが，20手前後の解を見つけることができます．

## 概要
[世界キューブ協会\(World Cube Association, WCA\)](https://www.worldcubeassociation.org/)の3×3×3 最小手数\(3x3x3 Fewest Moves\)で用いられる[DR\(Domino Reduction\)](https://sanya.sweetduet.info/cube/fmc/)という解法を用いています．基本的な流れは以下の通りです．

1. EO \(Edge Orientation\)

エッジキューブの向きを揃えます．\<U D R L F2 B2\>の動きのみで揃えられるようになります．

2. DR \(Domino Reduction\)

エッジキューブおよびコーナーキューブの向きが揃っており，かつ中段にあるべきエッジキューブが全て中段にあるように配置させます．\<U D R2 L2 F2 B2\>の動きのみで揃えられるようになります．

3. HTR \(Half Turn Reduction\)

180°回転のみで揃えられる状態にします．つまり，\<U2 D2 R2 L2 F2 B2\>の動きのみで揃えられるようになります．

4. Finish

ルービックキューブを揃えます．

## 使い方

解きたいスクランブルを

> `String scramble = "R' U' F U R2 B2 D' F2 D2 L2 D' L2 U2 R U' B' F D2 R D B2 D U' R' U' F";`

のように，ダブルクオーテーション内に入力してください．

探索を打ち切る手数を

> `solveCube (scramble, 5, 7, 5, 5);`

のように，入力してください．ただし，それぞれEO，DR，HTR，Finishの手数を表しています．

## ソースコードについて

ソースコードの複製，改変，再配布などは自由です．ぜひ初心者である私にご教授願います．