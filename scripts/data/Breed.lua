FecaBreed = 1
OsamodasBreed = 2
EnutrofBreed = 3
SramBreed = 4
XelorBreed = 5
EcaflipBreed = 6
EniripsaBreed = 7
IopBreed = 8
CraBreed = 9
SadidaBreed = 10
SacrierBreed = 11
PandawaBreed = 12

INCARNAM_STATUES = {
    [FecaBreed] = {10300, 337},
    [OsamodasBreed] = {10284, 386},
    [EnutrofBreed] = {10299, 300},
    [SramBreed] = {10285, 263},
    [XelorBreed] = {10298, 315},
    [EcaflipBreed] = {10276, 311},
    [EniripsaBreed] = {10283, 299},
    [IopBreed] = {10294, 309},
    [CraBreed] = {10292, 299},
    [SadidaBreed] = {10279, 284},
    [SacrierBreed] = {10296, 258},
    [PandawaBreed] = {10289, 250}
}

ASTRUB_STATUES = {
    [FecaBreed] = {7398, 299},
    [OsamodasBreed] = {7545, 311},
    [EnutrofBreed] = {7442, 255},
    [SramBreed] = {7392, 282},
    [XelorBreed] = {7332, 337},
    [EcaflipBreed] = {7446, 299},
    [EniripsaBreed] = {7361, 192},
    [IopBreed] = {7427, 267},
    [CraBreed] = {7378, 338},
    [SadidaBreed] = {7395, 357},
    [SacrierBreed] = {7336, 198},
    [PandawaBreed] = {8035, 384}
}

---@param p Player
---@param destinations table < number, number[2] >
function teleportByBreed (p, destinations)
    local dst = destinations[p:breed()]
    p:teleport(dst[1], dst[2])
end
