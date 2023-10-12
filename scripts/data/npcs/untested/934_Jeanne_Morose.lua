local npc = Npc(934, 51)

npc.gender = 1
npc.colors = {16711417, 15135244, 9659800}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4162, {364})
    elseif answer == 364 then p:endDialog()
    end
end

RegisterNPCDef(npc)
