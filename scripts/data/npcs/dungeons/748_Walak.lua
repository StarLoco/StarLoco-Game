local npc = Npc(748, 1363)

npc.accessories = {0, 629, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local cell = p:cell()
    local orientation = p:orientation()

    if not (cell == 268 and orientation == 7) then
        p:ask(3165)
        return
    end

    --TODO: Morph en fantôme en fonction de la réponse
    if answer == 0 then
        p:ask(6754, {6151, 6225})
    elseif answer == 6151 then
        p:ask(6755, {6152, 6153, 6154, 6155})
    elseif answer == 6152 then
        p:teleport(8979, 236)
        p:endDialog()
    elseif answer == 6153 then
        p:teleport(8979, 236)
        p:endDialog()
    elseif answer == 6154 then
        p:teleport(8979, 236)
        p:endDialog()
    elseif answer == 6155 then
        p:teleport(8979, 236)
        p:endDialog()
    elseif answer == 6225 then
        p:endDialog()
    end
end

RegisterNPCDef(npc)
