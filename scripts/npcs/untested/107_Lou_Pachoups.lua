local npc = Npc(107, 9043)

npc.gender = 1
npc.accessories = {}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(360, {300})
    elseif answer == 300 then p:ask(368)
    end
end

RegisterNPCDef(npc)
