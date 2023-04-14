local npc = Npc(53, 9031)

npc.barters = {
    {to={itemID=765, quantity= 1}, from= {
        {itemID=289, quantity= 100}
    }}
}

local jobId = Farmer

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(30, {270, 272})
    elseif answer == 270 then p:ask(334, {271})
    elseif answer == 271 then
        if p:tryLearnJob(jobId) then
            p:ask(335)
        else
            p:ask(336)
        end
    elseif answer == 272 then
        if p:jobLevel(jobId) == 0 then
            p:ask(338)
        else
            p:ask(337, {273})
        end
    elseif answer == 273 then
        p:ask(339)
    end
end

RegisterNPCDef(npc)
